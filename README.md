# Rackspace Word Count
Create a multi-threaded application that will read a text file and does a word count

**Table of Contents** 

- [System Design](#System-Design)
- [How to run](#How-to-run)
- [Configuration](#Configuration)
# System Design
Assume:
- The text file was too big to load to memory
- The word count process was more faster than data download process

So, the solution is:
- Use `Map` `Reduce` logic to do word count, and use `Producer` and `Consumer` to do memory manage
- For `Producer`, it just download part of data to `data pool` each time, once the `data pool` is full, the `Producer` will jump in `await` status until there has data removed from `data pool`
  - Assume each executor can process `m` size of data, we can update [max_size_each_task](#Configuration) in Configuration 
  - And assume we have `n` executors, we can update [executor_number](#Configuration) in Configuration
  - So, we can set `urlConn.setRequestProperty("Range", "bytes=" + start + "-" + end)` and use `urlConn.getInputStream()` to download `m * n` size of data each time
  - Assume the memory limit is `T`, so, the `data pool`'s max size will be `T / (m * n)`
- For `Consumer`, there has `n` executors run at same time, they consume data from `data pool`, the `data pool` will use `new ByteArrayInputStream(data, offset , max_size_each_task)` to generate stream for each executor, each executor has different `offset` and share same byte array `data`, so, there has no data copy.

![System Design](https://github.com/fengxucn/rs_homework/blob/master/docs/SystemDesign.png)
# How to run
Download `word_count` to you local and run

`./bin/run.sh`

you wll find all the result at

`./result/`

# Configuration
You can find the config file at

`./config/config.properties`

```
 #the max size of text file can load to each task, default is 64k, can set to 1m or else
 max_size_each_task = 64K
 
 #remove_stop_words = true
 stop_words = a,as,at,the,of,be,been,being,by,can,cant,did,didnt,do,does,doesnt,doing,dont,done,eg,et,\
              etc,ex,for,and,to,in,is,that,it,her,or,he,his,not,she,was,but,on,with,has,him,had,we
 
 #comma separate
 urls = http://www.gutenberg.org/files/2600/2600-0.txt
 
 topN = 5
 
 #can support max 10 executors
 executor_number = 8
```
# Word Count
Create a multi-threaded application that will read a text file and does a word count

**Table of Contents** 

- [System Design](#System-Design)
- [How to run](#How-to-run)
- [Configuration](#Configuration)
## System Design
Assume:
- The text file is too big to load to memory
- The word count process is faster than data downloading process

So, the solution is:
- Use `Map` `Reduce` logic to do word count, and use `Producer` and `Consumer` to do memory management
- For `Producer`, it just download part of data to `data pool` each time, once the `data pool` is full, the `Producer` will jump in `await` status until there is data removed out `data pool`
  - Assume each executor can process `m` size of data, we can update [max_size_each_task](#Configuration) in Configuration 
  - And assume we have `n` executors, we can update [executor_number](#Configuration) in Configuration
  - So, we can set `urlConn.setRequestProperty("Range", "bytes=" + start + "-" + end)` and use `urlConn.getInputStream()` to download `m * n` size of data each time
  - Assume the memory limit is `T`, so, the `data pool`'s max size will be `T / (m * n)`
- For `Consumer`, there is `n` executors running at the same time, they consume data from `data pool`, the `data pool` will use `new ByteArrayInputStream(data, offset , max_size_each_task)` to generate streams for each executor, each executor has different `offset` and shares same byte array `data`, so, there is no data copy.

**Q&A:**
- How the execution time could be improved and other scaling techniques
  - The bottleneck for the solution above is `Producer`, we can use cluster to speed up
  - we also can use `Spark` to improve the performance
- How would you re-architect your program if the input size is not ~ 3MB, but 1 TB
  - The `Producer` and `Consumer` solution can handle any size of data, because `Producer` just load part of data each time.
- Stop Words
  - Stop words like `the`, `is`, `are` they have high frequency for all text files, so there is no meaningful to count them , we can close this function by set [remove_stop_words](#Configuration) = false or add some more stop words to [stop_words](#Configuration)
- Some Concerns
  - Because we split the text file by size, so, some word will split to two words, so, the final result will has minor mistake, the `error rate` will be: `error rate <= 3 * (Total size / max_size_each_task) / Total words`
  - The remote server not support `urlConn.setRequestProperty("Range", "bytes=" + start + "-" + end)`
    - The `Producer` can download all the data to the local disk, assume the disk can hold all the data, if not, we can use `HDFS`
  - How about some `executor` crashed
    - In order to make the system more reliable, we need add some `Exception` handle logic, when some `executor` down, we need send the data to other `executor` to run.
  - How about the count result is too big to stay in memory
    - We can write the result for each task to the disk, and load them to `Reduce` process  
  

![System Design](https://github.com/fengxucn/rs_homework/blob/master/docs/SystemDesign.png)
## How to run
Download `word_count` to you local and run

`./bin/run.sh`

you wll find all the result at

`./result/`

## Configuration
You can find the config file at

`./config/config.properties`

```
 #the max size of text file can be loaded to each task, default is 64k, can set to 1m or else
 max_size_each_task = 64K
 
 remove_stop_words = true
 stop_words = a,as,at,the,of,be,been,being,by,can,cant,did,didnt,do,does,doesnt,doing,dont,done,eg,et,\
              etc,ex,for,and,to,in,is,that,it,her,or,he,his,not,she,was,but,on,with,has,him,had,we
 
 #comma separate
 urls = http://www.gutenberg.org/files/2600/2600-0.txt
 
 topN = 5
 
 #can support max 10 executors
 executor_number = 8
```

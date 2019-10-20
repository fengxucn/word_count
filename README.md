# Rackspace Word Count
Create a multi-threaded application that will read a text file and does a word count

**Table of Contents** 

- [System Design](#SystemDesign)
- [How to run](#Howtorun)
- [Configuration](#Configuration)
# System Design
If you want to embed images, this is how you do it:

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
 
 #escape_stop_words = true
 stop_words = a,as,at,the,of,be,been,being,by,can,cant,did,didnt,do,does,doesnt,doing,dont,done,eg,et,\
              etc,ex,for,and,to,in,is,that,it,her,or,he,his,not,she,was,but,on,with,has,him,had,we
 
 #comma separate
 urls = http://www.gutenberg.org/files/2600/2600-0.txt
 
 topN = 5
 
 #can support max 10 executors
 executor_number = 8
```
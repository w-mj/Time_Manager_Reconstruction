# TimeManager first reconstruction

## 2017-8-19
+ initialize project
+ basic process control
+ network module

## 2017-8-20
+ item module
+ user module
+ use android's message loop substitude my own message loop
+ import shcedule fragment, activity fragment

## 2017-8-27
+ reconstruction complete
+ refresh week view
+ set start time of a day

## 2017-8-28
+ make server works correctly. (when user modifies his own affair and save it to server, the server will check if the affair is only owned by him, if not, it will create a new affair for himself, and others affair will not be influence.
+ add affair's dialog (first step)

## 2017-8-30
+ simpleDateFormatter may cause some error when works under mutiple threads everiment. so remove static formatter in MyTools, create a new formatter when it is used instead.
+ change logical again. server will return whole while change information of affairs in json. 
+ add static tool function to Item and Time, in order to parse json directly.

## 2017-9-2
+ comlete add time module
+ color on schedule
+ you can get item through time directly now!
+ remove action bar
+ make week selecter avaliable.
+ fix number to chinese function again. fxxk

## 2017-9-15
+ finally, it could login to aao correctly. (beause I forget to flush output stream before = =)

## 2017-9-17
  I have tried to reconstruct Time class in order to get easier to use. But give up finally due to its too complex,  it will put on process in some days later.
  Download syllabus from Academic affair office is almost can be use.
  There is a stange issue is that when I put date in a Calendar, it will change automatically. BUT when I run the code step by step in debug mode and watch the variable, It doesn't change anymore, also, if some log code is added to watch it, it preform normaly too. Dose it quantum? LOL.
 

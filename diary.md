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

JimJam
======

A Job Queue/Manager for Android.

Current Status
--------------

* You can add a custom logger to the JobManager through its builder. The JobManager does not use this. ^_^
* Sample application lets you tap on 3 different buttons to create jobs which "get added to the JobManager". The JobManager currently ignores these stub Jobs, but they do have their parameters configured and are ready to go! They don't broadcast messages yet though..
* Jobs are now creatable with a JobParameters object which configures the job. Here you provide whether the job is persistent, requires the network, and the jobs "priority". Right now this is an enum. This is very likely to change to just some integer later.

Todo
----

This is not necessarily in any order.

* Fix the project directory structure so it's not so flat! :) Right now all modules have all java files in the root package.
* Add a few methods to Job like "run()" and "added()".
  * Implement these in the sample application.
* Persist jobs to disk through Serializable-ness.
  * Learn about Java Serialization. Is this the best approach? Do we maybe want to use a JSON parser? I kind of want to avoid any dependencies for this project if possible.
  * Look into native Android json serialization, as well as gson and other libraries.
  * Figure out the best way to persist the Jobs once serialized. (Probably an Android Sqlite DB?)
    * Learn how SQLite DB's work in Android if this is the decided approach.
* Implement the "requires network" in the JobManager.
* Actually have the JobManager queue and consume jobs instead of ignoring calls to addJob().
* Configure jobs in sample app to do three things.
  1. **Sleep Job**: Will sleep for 20 seconds and broadcast a "sleep job complete!" message. Useful to test persistence. If you reboot the phone after tapping the button, will the task still complete?
  1. **Requires Network Job**: Will broadcast a "Network job complete, beep boop!" when the job is run. If you turn off wifi/data, the job should not run until it's turned on.
  1. **High Priority Job**: Broadcast message "High Priority Job complete". This should supersede other jobs running.
  
Where does the JobManager live?
-------------------------------
Wherever you want! I would recommend putting it in a service which you launch when your application starts, however you can have it live in your app if you want to keep it simple. Jobs will be run on a pool of threads in either situation which you configure when you initialize the JobManager. If you put the JobManager in a service you can [start the service when the phone boots](http://stackoverflow.com/questions/2784441/trying-to-start-a-service-on-boot-on-android).

How do I tell the calling Activity about progress of a running job?
-------------------------------------------------------------------

Use a local broadcast or bus system to send messages from the Job. The JobManager will not provide a direct mechanism to achieve this.

License
-------

	Copyright 2015 Coltin Caverhill

	License to be determined, please stand by.

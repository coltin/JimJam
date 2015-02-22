JimJam
======

A Job Queue/Manager for Android.

Current Status
--------------

* JobManager can be created+configured through JobManager.Builder. Do this when your Application gets created (either directly fromt he App, or a service you start there). It can live in either.
* Sample application lets you tap on 3 different buttons to create jobs which get added to the JobManager. These jobs will be put in an executor and run. A Toast is made when they are finished. The Network Job will not wait for the network (yet) and priority is ignored.
* Jobs are now creatable with a JobParameters object which configures the job. Here you provide whether the job is persistent, requires the network, and the jobs "priority".
* Job priority is specified as an enum. This will likely change to give more flexibility, and you can create your own enum of Job Priority if you want. I'm just experimenting with it for now.

Todo
----

This is not necessarily in any order.

* Fix the project directory structure so it's not so flat! :) Right now all modules have all java files in the root package.
* **NEXT** Persist jobs to disk through Serializable-ness.
  * Learn about Java Serialization. Is this the best approach? Do we maybe want to use a JSON parser? I kind of want to avoid any dependencies for this project if possible.
  * Look into native Android json serialization, as well as gson and other libraries.
  * Figure out the best way to persist the Jobs once serialized. (Probably an Android Sqlite DB?)
    * Learn how SQLite DB's work in Android if this is the decided approach.
* Recover serialized jobs when JobManager is recreated.
* Implement the "requires network" in the JobManager.
  
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

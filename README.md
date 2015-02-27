JimJam
======

A Job Queue/Manager for Android.

Current Status
--------------

* JobManager can be created+configured through JobManager.Builder. Do this when your Application gets created (either directly fromt he App, or a service you start there). It can live in either.
* Sample application lets you tap on 3 different buttons to create jobs which get added to the JobManager. These jobs will be put in an executor and run. A Toast is made when they are finished. The Network Job will not wait for the network (yet) and priority is ignored.
* There is now a button to print logs (to logcat) of all the saved jobs. If you reboot your device, they will still show up! Jobs are not removed when they complete (yet). The "High Priority" job is  not persisted.
* Jobs are now creatable with a JobParameters object which configures the job. Here you provide whether the job is persistent, requires the network, and the jobs "priority".
* Job priority is specified as an enum. This will likely change to give more flexibility, and you can create your own enum of Job Priority if you want. I'm just experimenting with it for now.
* Unfinished persisted jobs will be restarted when the JobManager is created.

Todo
----

This is not necessarily in any order.

* Fix the project directory structure so it's not so flat! :) Right now all modules have all java files in the root package.
* Implement the "requires network" in the JobManager.
* Implement the idea of "job retries", and remove a job when it runs out of retries.
* Do proper queueing/scheduling.
 * Take priority into consideration.
 * Use the "requires network" knowledge for jobs that require them.
* Ensure DB actions are done in the background.
* Add job lifecycle methods so that application developers can hook into them as desired.
  
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

JimJam
======

A Job Queue/Manager for Android.

Current Status
--------------

* JobManager can be created+configured through JobManager.Builder. Do this when your Application gets created (either directly from the App, or a service you start there). It can live in either.
* Sample application lets you tap on 3 different buttons to create jobs which get added to the JobManager. These jobs will run in background threads. A Toast is made when they are finished. The Network Job will not wait for the network (yet) and priority is used. If all threads are full of running jobs, a high priority job will NOT pre-empt a running job. If it's the highest priority Job it will run next.
* Sample app has a button to print logs (to logcat) of all Jobs saved to disk. If you reboot your device, the persisted jobs will still show up. Jobs are removed when they complete. When an app is restarted persisted jobs will be added back into the queue. Note: The "High Priority" job is configured to be not-persistent.
* Jobs are creatable with a JobParameters object which configures the job. Here you provide whether the job is persistent, requires the network, and the jobs "priority".
* Job priority is specified as an enum. This will likely change to give more flexibility.
* Jobs that require network access will not be run until the network is available. JobManager receives "network connected" events which it uses to push network jobs back into the priority queue.
* Age of job is NOT taken into consideration for priority, only the priority flag passed on job creation.
* When a job fails (throws an exception during run()) a "shouldRetry()" method will be called on the Job (which it must implement). It will be pased the raised Exception as well as the retry count.

Todo
----

This is not necessarily in any order.

* Fix the project directory structure so it's not so flat! :)
* Create a section of this READEME that is dedicated to the sample application. How it works, how it can be used, etc.
* Make calls to JobManager async (mostly done with JobManagerThread).
* Add job lifecycle methods so that application developers can hook into them as desired. (partially done)
* Allow a job to cancel? Seems like this is unnecessary given a job can throw an exception to cancel itself. Having something outside the job cancel might be useful.
* If a persistent job fails to complete and it will be re-run, first save its current state to disk so that running it again will have th previous state. This should be documented. Maybe this behaviour can be confifured.
* Write a section on performance (better done after more core tasks are completed) [memory, cpu, threading].
* When starting up the JobManager and jobs are added from disk, jobs should be added in priority order; or the ExecutorService should be locked from spinning up Threads until all these jobs have been added.
* Updated sample application so that logcat is not required to analyze jobs.
* Change NetworkBroadcastReceiver so that it runs in a background thread.
* Use github issues and wiki instead of having everything in the Readme.

Where does the JobManager live?
-------------------------------
Wherever you want! I would recommend putting it in a service which you launch when your application starts, however you can have it live in your app if you want to keep it simple. Jobs will be run on a pool of threads in either situation which you configure when you initialize the JobManager. If you put the JobManager in a service you can [start the service when the phone boots](http://stackoverflow.com/questions/2784441/trying-to-start-a-service-on-boot-on-android).

How do I tell the calling Activity about progress of a running job?
-------------------------------------------------------------------

Use a local broadcast or bus system to send messages from the Job. The JobManager will not provide a direct mechanism to achieve this.

License
-------

	Copyright 2015 Coltin Caverhill

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	   http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

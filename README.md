JimJam
======

(This project is under active development, and is not ready to be used. Fork at your own risk!)

A Job Manager for Android. Do you have something you need to guarantee happens, even if the device reboots or crashes before the task finishes running? JimJams got you covered! Want to do something once you have network connectivity, even if the network is currently dead? Not a problem, JimJam specializes in internet tube awareness. Want to feel fulfilled and happy in your life? Wow, JimJam does too! Such good friends already.

What is actually going on here?
-------------------------------
Great question! It's almost as if I wrote that, crazy!

[Horse farts](https://www.youtube.com/watch?v=jMyL0HdXPuc) are amazing. When your users tap your applications well designed "post video" button to share their latest animal fart video, what should happen when the users data is down or your server is being DDOS'd by every script kiddy with a botnet?

The answer is not "make a toast or notification making the user sad and telling them we failed in our duty to upload horse farts". The answer is "Just let JimJam handle it". All you need to do is create a Job which defines what you want to happen, and JimJam handles the rest.

Creating the JobManager
-----------------------
The best example is to look at the [JimJam Sample Application](jimjam-sample/src/main/java/com/coldroid/jimjam/sample), and in particular look at the [SampleApplication class](jimjam-sample/src/main/java/com/coldroid/jimjam/sample/SampleApplication.java). In a few lines the JobManager is configured and ready to go. I would recommend making a Utility method to make your life easier, but creating a job is just a matter of doing this:

```java
SampleApplication.getJobManager().addJob(someJob);
```

Some Random Information (before I create the wiki and actual documentation)
---------------------------------------------------------------------------

* JobManager can be created+configured through JobManager.Builder. Do this when your Application gets created (either directly from the App, or a service you start there). It can live in either.
* Sample application lets you tap a few different buttons to create jobs which get added to the JobManager. These jobs will run in background threads. A Toast or logcat message is made when they are finished. If all threads are full of running jobs, a high priority job will NOT pre-empt a running job. If it's the highest priority Job it will run next.
* Sample app has a button to print logs (to logcat) of all Jobs saved to disk. If you reboot your device, the persisted jobs will still show up. Jobs are removed when they complete. When an app is restarted persisted jobs will be added back into the queue. Note: The "High Priority" job is configured to be not-persistent.
* Jobs are creatable with a JobParameters object which configures the job. Here you provide whether the job is persistent, requires the network, hash a label, and the jobs "priority".
* Job priority is specified as an enum. This will likely change to give more flexibility.
* Jobs that require network access will not be run until the network is available. JobManager receives "network connected" events which it uses to push network jobs back into the priority queue.
* Age of job is NOT taken into consideration for priority at this time, only the priority flag passed on job creation.
* Multiple jobs with the same label will be executed one at a time. The ordering property is still the same as other jobs.
* When a job fails (throws an exception during run()) a "shouldRetry()" method will be called on the Job (which it must implement). It will be passed the raised Exception as well as the retry count.

What happens when we add a job?
-------------------------------

Also known as "what happens when your application or service calls your JobManager instances addJob() method".

Note: Now that there is job labelling, this is slightly out of date. I added a TODO to update this.

1. Your job is posted to the JobManager's background thread.
2. The background thread will process the addJob() request.
3. If the job is "persistent" it will be written to an SQLite database (ie, disk). After this point if your application crashes, is killed, or the device reboots, your job will still be processed the next time you start the JobManager, not extra effort/work is required on your end.
4. The job is passed to the PriorityThreadPoolExecutor for processing.
 * If there are less than max threads currently executing other jobs then your job will start executing right away.
 * Otherwise it will be added to a priority queue (managed by the ThreadPoolExecutor). When a thread finishes executing a job, it will start executing the highest priority job from the queue (if there are any).
  * If all threads are executing low priority jobs, your high priority job will need to wait for one of them to finish executing before it will run.

Todo
----

This is not necessarily in any order.

* Fix the project directory structure so it's not flat.
* Wiki. Wiki all the things. Plus these:
 * Seciton on performance: CPU, Memory, and Threading.
 * One on PriorityThreadPoolExecutor.
 * Labelling. Simple to use, but implementation is more complicated than it looks.
* Audit JobManager calls, and make sure everything is processed in the background.
* Audit the network monitoring. Can we do it better?
* Add job lifecycle methods which can overriden. Things like "onAdded()", "onLoaded()", etc.
* When a persistent job run fails, re-serialize it to disk to save its state.
* When starting up the JobManager and jobs are added from disk, jobs should be added in priority order.
* Updated sample application so that logcat is not required to analyze jobs.
* Update the "What happens when we add a job?" section to discuss labelling.

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

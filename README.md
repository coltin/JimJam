JimJam
======

A Job Queue/Manager for Android.

Current Status
--------------
Currently you can add a custom logger to the JobManager through its builder.
The JobManager does not use this. ^_^

Todo
----
* Create Jobs with custom parameters, such as persistence, priority, network requirement, etc.
* Persist jobs to disk through Serializable-ness.
* Add a "Job" to JimJam-Sample when a button is tapped. This job will sleep for 20 seconds before broadcasting a "job complete!" message. Rebooting the phone before "job complete" finishes should still allow for the job to complete once the sample is relaunched.
* Enable "requires network" for jobs.
* Create a button in JimJam sample that when tapped will create a job that requires network connection. When it has network it will broadcast a message saying "has network access!". This should be tested with network disabled, and then re-enabled.

License
-------

	Copyright 2015 Coltin Caverhill

	License to be determined, please stand by.

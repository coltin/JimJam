JimJam
======

A Job Queue/Manager for Android.

Current Status
--------------
* You can add a custom logger to the JobManager through its builder. The JobManager does not use this. ^_^
* Sample application lets you tap on 2 buttons to create jobs which "get added to the JobManager". The JobManager just ignores these Jobs and the Jobs are empty Job classes.

Todo
----
This is not necessarily in any order.

* Create Jobs with custom parameters, such as persistence, priority, network requirement, etc.
* Persist jobs to disk through Serializable-ness.
* Enable "requires network" for jobs.
* Configure jobs in sample app to do two things.
  1. **Sleep Job**: Will sleep for 20 seconds and broadcast a "sleep job complete!" message. Useful to test persistence. If you reboot the phone after tapping the button, will the task still complete?
  1. **Requires Network Job**: Will broadcast a "Network job complete, beep boop!" when the job is run. If you turn off wifi/data, the job should not run until it's turned on.

License
-------

	Copyright 2015 Coltin Caverhill

	License to be determined, please stand by.

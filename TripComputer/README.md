(c) Ben Wilcock & SoaGrowers.com

-------------------------------------------------
Trip Computer Free 1.0.49:

Made $ and c the default currency given most users are in the US.
Moved up to the latest version of Google Play Services to improve the accuracy of LOW POWER tracking.
Moved to the Google Analytics API within Google Play Services to reduce dependencies.
Dropped support for Froyo - not supported in Google Play Services any more, sorry.

    --User is shutting down the App UI, but services are left running
    19:41:12.146  13679-13679/tripcomputer I/tripcomputer.TripComputer? STOPPED TripComputer Activity: 1107344720
    19:41:12.153  1258-4401/? W/ContextImpl? Calling a method in the system process without a qualified user: android.app.ContextImpl.sendBroadcast:1244 android.content.ContextWrapper.sendBroadcast:365 com.motorola.motocare.util.TriggerHelper$TriggerBuilder.send:76 com.motorola.motocare.internal.frameworkevents.PauseResumeTrigger.handleFrameworkEvent:53 com.motorola.motocare.internal.frameworkevents.FwEventMonitor$FrameworkListener.processFrameworkEvent:114
    19:41:12.645  13679-13679/tripcomputer I/tripcomputer.TripComputer? DESTROYING TripComputer Activity: 1107344720
    19:41:12.646  13679-13679/tripcomputer I/tripcomputer.services.JourneyServiceConnectionManager? Activity 1107344720 is STOPPING the JourneyService...
    19:41:12.652  13679-13679/tripcomputer I/tripcomputer.services.JourneyServiceConnectionManager? IGNORING request to STOP the JourneyService - Journey in progress.
    19:41:12.655  13679-13679/tripcomputer I/tripcomputer.services.ActivityServiceConnectionManager? Activity 1107344720 is STOPPING the ActivityService...
    19:41:12.657  13679-13679/tripcomputer I/tripcomputer.services.ActivityServiceConnectionManager? IGNORING request to STOP the ActivityService - Activity in progress.
    19:41:12.659  13679-13679/tripcomputer I/tripcomputer.TripComputer? DESTROYED TripComputer Activity: 1107344720

    --The UI has gone. Next activity update arrives at the IntentService and gets Logged...
    19:41:19.703  13679-14095/tripcomputer I/tripcomputer.services.ActivityUpdateIntentService? The most probable user Activity is still (50)

    -- The system kills the IntentService?
    19:41:19.704      969-979/? I/ActivityManager? Killing 13679:tripcomputer/u0a152 (adj 0): remove task
    19:41:19.706    1258-4401/? W/ContextImpl? Calling a method in the system process without a qualified user: android.app.ContextImpl.sendBroadcast:1244 android.content.ContextWrapper.sendBroadcast:365 com.motorola.motocare.util.TriggerHelper$TriggerBuilder.send:76 com.motorola.motocare.internal.frameworkevents.ProcessKillTrigger.sendTrigger:147 com.motorola.motocare.internal.frameworkevents.ProcessKillTrigger.handleFrameworkEvent:164

    --Boom, everything else has gone but nothing in the Log - system schedules the Crashed Services for restart
    19:41:19.727     969-1273/? W/ActivityManager? Scheduling restart of crashed service tripcomputer/.services.JourneyService in 1000ms
    19:41:19.736     969-1273/? W/ActivityManager? Scheduling restart of crashed service tripcomputer/.services.ActivityService in 1000ms

-------------------------------------------------
Trip Computer Free 1.0.48:

Added better currency support for New Zealand, Ireland & Jamaica


-------------------------------------------------
Hello everyone,

As an IT freelancer I was on the lookout a simple way to calculate mileage expenses. There are many apps that can do this, but
all the ones I tested would either drain my battery in just a few hours or were designed for other markets where different tax rules apply
(USA in particular). So when I couldn't find something suitable, I decided to write my own...

Trip Computer is the [only?] mileage expenses tracking App for Android phones to feature a LOW POWER 
tracking option. This means that you can use it all day without draining your battery!

https://play.google.com/store/apps/details?id=tripcomputer

In LOW POWER mode, Trip Computer can track your position using the cellphone 
and wifi network - and yet it's surprisingly accurate, especially for long car journeys!

In addition, because the mileage charge rate can be set to whatever you want, you can use it 
both below and above the HMRC 10K mileage threshold (£0.45p per mile, £0.25p per mile, etc.). 
You can use it for business or for leisure - it's also a great companion for walking biking etc. 

It's completely free to download and it works with a wide range of Android devices from 
2.3 (Gingerbread) onwards.

If you think it could help you out, feel free to download it and give it a try!


Hi Guys,

Can I suggest an App for review: https://play.google.com/store/apps/details?id=tripcomputer

Trip Computer is the [only?] location, distance and cost tracking app for android to feature a LOW POWER (non-gps) 
tracking facility. It's designed primarily to help employees and drivers manage their mileage expenses, but it can 
also be used a simple leisure tracker when cycling, walking etc.

Because it has a LOW POWER option, unlike most other tracking apps, it won't drain your battery in just a few hours, 
so you can use it to track really long trips, even trips that take many days. And even though the LOW POWER option 
doesn't use your GPS hardware, it's still surprisingly accurate. In addition, because the charge rate can be set to 
whatever you want, it's as useful all over the world (and available in the US, Canada, Australia, UK, etc.)

I'm sure that your readers would be interested to hear about this unique tracker, and I'd be very grateful if you 
could help me get the word out that tracking doesn't need to be battery-sapping!

In summary, Trip Computer:-

* Is the [only] LOW POWER location tracking app for Android.
* Can track the length, distance, time and cost of any journey
* Let's you set your own 'mileage rate' - it's totally flexible 


-------------------------------------------------
Trip Computer Free 1.0.47:

Small tweaks to the online help.
AndroidStudio 0.5 has updated the Gradle version to 0.9.
Changed the Version text to include a copyright notice.
Fixed issue with ProGuard that was affecting the Share menu in builds.
Tested share menu on emulator.

-------------------------------------------------
Trip Computer Free 1.0.44:

Added more help information to the online site (FAQ's and other info).

Added new setting to hide/show 'Additional Cards' (lat/long etc.) so you can keep your screen tidy.
Added categories to the settings page - 'Basic and Advanced'

Added a new attributes to Journey for 'Current Altitude' - only available when the Location provider on
the device supplies it. Dev mode only.

Did some testing on older phones and phones without Google Play Services. Found some potential issues
and fixed them. Removed some old unused attributes.



-------------------------------------------------
Trip Computer Free 1.0.43:

Code. 
More tidying up before launch.
Changes to the LogLine class to try an improve performance a little.
Major upgrade to the way that the Service is managed. 
Created or modified Binder/Client/Actions/Connection/ConnectionManager.
Modified the settings activity and the way TripComputer's UI spots theme changes.
Added new Asserts. Updated the Launcher icon using the built in Wizard in AS.
Made the share link point to a website (just help for now).

Online Help.
Big overhaul of the on-line Help page. 
Looks great, but make sure 'Javascript' is ON in your browser to get the full effect!

Drawings.
Fixed the 'reflection' edges so I could remove the black letterbox layer. 
Because the letterbox has gone it now generates transparent background correctly.
Created a banner format graphic for the store at the right size 1024x500.

-------------------------------------------------
Trip Computer Free 1.0.41:

Share Menu Item.
Will allow you to share the TripComputer app with other people (once published and out of Beta -
won't work yet).

UI
Added 2 new cards 'Location Accuracy' and 'Locations Used' so you can see a little of what's going
on behind the scenes. Made the on screen text darker/lighter for greater readability.

Log to file. 
A LogFile is now created in the ExternalStorage folder (if it's available on the device for writing).
It's stored under android/data/tripcomputer/files/Logs. 
It uses an Async task and a small message buffer so that it doesn't impede performance.

Persist Journey to File.
Saves the Journey in a file in the ExternalStorage folder (if it's available on the device for writing).
File content is RAW and in JSON format (not particularly human readable, sorry).

Stability.
Fixed a crash that could happen if the saved Journey couldn't be re-opened for any reason.
Tried to track down some more NullPointerExceptions

Preference defaults for new installs.
Default Location Accuracy is now set to 300m for new installs.

-------------------------------------------------
Trip Computer Free 1.0.39:

Code.
More attempts at stability improvement by removing some more NullPointer opportunities.
Downgraded Google Play Services requirement to v3 from v4 (for Holly & Beth's Froyo support).

UI.
Changed some of the card titles and the format of some card details.

Logging.
Added a new LogFile feature to developer devices (doesn't work on regular builds yet).
Requires READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE permissions in the Manifest.

-------------------------------------------------
Trip Computer Free 1.0.38:

Code
Refactored some of the startup tasks in onCreate().
Removed some of the old commented out code.
Did some hardening work following code review by FindBugs.
Refactored the JourneyService by introducing the LocationUpdateManager class which handles connecting
to Location services and initially handling updates on behalf of LocationListeners.

Testing
Added a batch file to run the 'Monkey'

Build
Removed the 'Free' and 'Pro' Variants from the build. Moved the associated config files contents into the
regular build files.


-------------------------------------------------
Trip Computer Free 1.0.36:

Fixed a but with the STAY AWAKE feature that kept the screen on incorrectly.
Started to work on the the in-app payments feature, so App now requires the BILLING permission.
Added new UI cards to display Lattitude, Longitude and direction of travel.
Tweaked the UI slightly including new dimmed nav-bar in Night Mode.
Added a new Preference called 'Accuracy Threshold' which lets the user select how accurate a location fix should be.

Simplified some of the Preferences by making their names and summaries more user friendly.
Removed the animated button bar (affects users on Honeycomb onwards).
Cleaned up the background Service and Notification code.
Added new developer cards to show Location Accuracy and Counts (not visible to standard users).

-------------------------------------------------
New in Trip Computer Free 1.0.24:

Added support for Android 4.2.2 'Froyo' for Richard, Sam, Bethany and Holly-Ann.

-------------------------------------------------
New in Trip Computer Free 1.0.23:

A fading transition has been added to button bar (needs Android v11).
Cleaned up the themes/styles and removed the unused icons.
Small tweaks to the order of the cards (again).

-------------------------------------------------
New in Trip Computer Free 1.0.22:

Upgraded the Google Play Services version to 4.1.32.
Added animated transitions to the Cards (needs Android v11).
Changed the order of the cards in portrait view.
Restyled the START and STOP buttons to remove icons and make them thinner.
Darker background and ActionBar in 'night mode'.
Fixed the bug that caused Preferences screens to get blue highlights.
Added orange highlights in night mode that are slimmer.
Consolidated a lot of resources and added some new styles and dimensions.

-------------------------------------------------
New in Trip Computer Free 1.0.21:

Added support for Android API 9 devices. 

UI modifications for slightly clearer card titles, a different settings icon, and a fix to the annoying blue line appeared on the settings screen. The ActionBar background is also slightly cleaner as it now has it's own custom drawable.

There has also been a change to the launch-mode - we're now running as a 'Single Instance', so look out for any bugs that might appear following this fundamental change.

-------------------------------------------------
What's New in Trip Computer Free 1.0.20:

Bug Fix.
For issues with Notifications not being removed, introduced in 1.0.19.

-------------------------------------------------
What's New in Trip Computer Free 1.0.19:

Bug Fix.
Fix for Null Pointer Exception reported when app switches between start/stop and preferences/settings multiple times.

-------------------------------------------------
What's New in Trip Computer Free 1.0.18:

Analytics tracking.
I've added analytics tracking so that I can get better crash reports. This requires new permissions so that the 
tracking information can be sent over the internet. All data sent is anonymous.

Version Information.
I've added a menu option to display the build version number on request.

Logging.
Practically all logging has been removed from the 'release' build type.

User Interface.
I've changed the default visibility of the Lattitude/Longitude cards because they were popping up on devices that were not
in developer mode. This should not happen. You should no longer see Lat/Long at all.

-------------------------------------------------
What's New in TripComputer on 10-01-2014:

Alpha Release to Play Store.

Settings.
Added new 'Stay Awake' setting which prevents the screen dimming while the app is at the front. Handy if you use the app while driving.
Added a settings button for the ActionBar to allow faster access to settings from the app (handy when driving).

User Interface.
Implemented a totally new 'Card Based' UI look and feel, similar in style to Google Now. Each data item is a card which has a slight shadow. Different colours are used for day and night, otherwise it would be a bit too glary at night. The change to cards has allowed me to offer 
more verbose card descriptions and also gives the data more room to be displayed. Works right back to gingerbread on practically 
the same layout, so maintenance should be simpler in theory. There are Portrait and Landscape versions.

Signed and ProGuarded.
The app has been signed now and has full 'Release' status as far as the build is concerned. 
The source is also ProGuard obvuscated.

-------------------------------------------------
What's New in TripComputer on 03-01-2014:

Bug Fixes.
Fix for the Crash on rotate issue - seemed to be related to a race condition where a variable was being accessed
before it had been sucessfully bound by an OS callback.

Features.
Upgraded the 'show last journey' feature the Free version.

New Settings.
Added a new 'Night Mode' setting which switches the Theme for a darker one in onCreate() when the setting is checked. Handy
for night time use or in the car.

Layout Changes.
Removed the custom landscape layout. Lanscape will still work, but the layout is no longer customised. I need a better layout
but for now this will do. I smartened up the Gingerbread screen to bring it inline with Honeycombe onwards where possible.
Button bar is a little thicker, easier for fatter fingers (no longer follows recommended style though, so I mar revert).
Used a little more of the screen real estate by removing some more of the layout padding. Made horizontal lines go right
across the screen (where possible).

Internal Coding Changes.
Managed to reduce the amount of code overall and improve general SOC and cohesiveness by changing the way that presentation 
logic was handled.

-------------------------------------------------
What's New in TripComputer on 02-01-2014:

Location - 'Locality' readout.
The Locality reverse lookup can fail a *LOT*. Most common issue is that the OS timout for this check is very short 
probably in order to prevent ANR's etc. in badly written code. There is also suggestions online of overactive clients
being 'blacklisted' by the server. So I've made a few updates....

	1. Reverse lookups of Localities from Lat/Long now only happens once in any 60 second period.
	2. If the lookup fails an Info level line is written to the log.
	3. If the lookup fails, only the last known good location or blank is shown.

Therefore 'connection down' will no longer be seen in the UI - it just stays blank.

Layout Changes.
The layout can now flip. To do this I've implemented Fragments to allow reuse in different layouts.
The landscape view is a bit rough, but at lease there is one now - handy in th car.

New Pro Variant.
I've sent you a 'TripComputer Free' and a 'TripComputer Pro' APK. There is just one difference between Free and Pro,
Pro will load up the Information from the last Journey you took and display it when the app boots from a cold
start (i.e. a start where there is no background service). This is controlled by a feature sheet and a
build variant.

Notes.
All these changes have been made rather quickly. Therefore there may be some error's that I've not identified
in my own testing. Normally I'd give it a day or two, but in this case I thought I'd get something out quick and
hope for the best. Let me know if there are issues and I'll sort them.

-------------------------------------------------
What's New in TripComputer on 28-12-2013:

Installation. 
You may need to bin your old copy of TripComputer first due to some issues with the way the preferences have been keyed.

Settings.
In the menu there is a new settings feature. This will allow you to switch between Miles, Kilometers and Meters and change the amount 
charged per unit of distance travelled. On Gingerbread, because there are no NumberPicker widgets, I've opted for a TextBox. The keys
allowed have been locked down, but the validation is probably quite poor. It will try and ignore amounts it doesn't recognise. More testing
required here, but it's beyond frustrating on the emulator...

There is also a new Maximise Battery option which is set to CHECKED by default. Switching to UNCHECKED turns on the high low power location tracking method (which uses the GPS and satellite data). Maximise Battery should be the best method in most situations, but in places where mobile signals or wi-fi are sub-optimal, users can switch to the higher power (battery sapping) mode. Note: If you're using sat nav on your phone at the same time as TripComputer (e.g. google maps navigation etc.) high power mode will already be on and your battery drain will be increased anyway. 

All settings take immediate effect, whether a Journey is running or not. For example, wwitching the charge on a stopped journey, will still recalculate the charge. Switching to Meters while running will recalculate the distance travelled into Meters.

Help.
In the menu there is a new help command. This takes you to a browser window and connects to my help site for TripComputer on the web.
The content is only half finished, but I'll keep working on it between releases.

UI.
New default TripComputer icon has been added which is slightly richer and more colourful than before.

Service.
The option to Stop the Service manually has been removed. 
If the trip recording has been stopped, the Service will be removed automatically when the user stops the app.
This is in line with Google's App QA Standards. 

-------------------------------------------------
What's New in TripComputer on 16-12-2013:

Average Speed. 
New algorithm that should hopefully be a little bit more accurate than it was before. However, not that the average speed is calculated as distance covered over time taken, so if you leave the clock running but don't move much this will affect the average.

Preferences.
You can now switch your preference for Miles, Kilometres and Meters. In meters, no charge is calculated. You can do this at any time, you don't have to stop or start the trips or anything, it al just gets recalculated on the spot. Your preference is saved for next time. This will probably get moved to a proper Preferences activity, but for now it's entitled 'Distance Units' in the menu.

-------------------------------------------------
What's New in TripComputer on 10-12-2013:

Logcat & Toasts.
Logging should now be very quiet because I've implemented a new and more controllable logging strategy
which strips log lines based on a config file I supply to the build. I've also toned down the toasts
so the app is less chatty at startup and when switching between states etc.

Average Speed. 
I've implemented a new average speed calculation strategy rather than relying on the 
optional ones supplied by the OS. Now as long as you've been moving and the accuracy of the location updates has been good, you should get an average speed readout. In theory its reasonably accurate,
but it's only as good as the gps fixes accuracy from point to point. It will ignore bad accuracy ones.

Service.
The service now stays permanently in the background unless you physically stop it manually
in the App options. You can't do this if a trip's running.

General.
I'm planning for a release around the end of December. The only feature I want to add now is the ability
to control the pence per mile figure, although I may be forced to launch without it depending on time.


------------------------------------------------

APK is in the attached Zip alond with a batch file for windows which should install it for you if:-

* ADB is working from the command line (you can check with "adb version").
* Your phone is connected (you can check with "adb devices" - there should be something in the list).
* Your phone is in developer mode with "Allow USB debugging" set to "ON"

If it all works as intended, it should install and boot the App user interface for you. (There will also be a new icon in your App drawer for "trip Computer")

Release notes:-

Average speed is hit and miss...
This is because the low power method I've used doesn't always report the speed as part of the location data. Sometimes it does, and it probably does it consistently if a higher power location app (like Maps/Navigation) is already running. I can probably implement a rough workaround, but it will always only be a rough I think (because you could start a Journey but not go anywhere).

Service can only be closed when there is no Journey running...
This is intentional to prevent Journeys being impacted if the OS send the app to the background or destroys it to recover resources. I've implemented Toasts to tell you hat's happening if a service event fires - let me know if these are too intrusive or chatty for you. The service will stop if it's not needed and will start when the app starts. It's no problem to leave the service in the background if you want - it doesn't use much resource really.

User Interface...
I'm not keen on the buttons but hey, they work.
I'm not sure how it will scale. I tried it on my Nexus 7 and it looks OK, just lots of blank screen which is unsightly but it doesn't cock-up which is good. 
Gingerbread - the ActionBar menu items are missing, not sure why yet.

General usage...
You can leave it running for ages, as long as you want really. Battery use should be low and you can check this with Android 4.4 on the battery screen. You could even record a full weeks trip if you leave your phone on to charge. It's up to you how you use it really. You can travel how you want too - car, bike, train, plane, boat, foot, etc.
If there is a Journey running or the App is running somewhere, there is a notification icon left in the tray that lets you get back to the App quickly from the home screen.

Known (intended) Issues...
Journey data and Journey recording doesn't survive a reboot.
There is no History of Journeys.
There is no ability to pause and resume a Journey.
There are no configurable settings.
These features may come if the demand is there.

Full screen layout with Transparent Status Bar

Activities, the building block of any Android app. Something so simple, yet so complex.
Here we are going to talk about something similiar related to activities which looks very simple
from the outset but gets complex once we start thinking about supporting multiple API versions.
We will build a full screen layout with transparent status bar. I'm not going to talk about
why would you need a full screen layout and in what situations. That's a topic for another discussion.
However, here's a simple usecase. If you have ever seen any app with a map(like a ride-hailing app),
you would see that the map occupies the space below the status bar as well. The content of the layout
other than the map doesn't overlap with the system bar icons. Doesn't it look sweet?
So, we are just gonna recreate that UI. Something like this:

(Pic of the result on various API versions)

Let's start with the basics(i assume you already have created an Activity with a map) and so, let's set a theme for our activity:

Set the activity theme as Theme.AppCompat.Light.NoActionBar:
<style name="CustomTheme" parent="Theme.AppCompat.Light.NoActionBar"/>

And then apply theme to the activity as usual:
        <activity
            android:name="com.my.app.CustomActivity"
            android:theme="@style/CustomTheme">
        </activity>

Easy peasy!! Let's see what we have got.

I'm assuming your layout looks something like this:

<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                xmlns:app="http://schemas.android.com/apk/res-auto"
                android:id="@+id/content_container"
                android:layout_width="match_parent"
                android:layout_height="match_parent">

    <fragment
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:id="@+id/map"
            android:name="com.google.android.gms.maps.SupportMapFragment"/>

    <com.google.android.material.floatingactionbutton.FloatingActionButton
            android:id="@+id/fab1"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_alignParentStart="true"
            android:layout_alignParentLeft="true"
            android:layout_alignParentTop="true"
            android:layout_marginEnd="8dp"
            android:layout_marginRight="8dp"
            android:scaleType="center"
            app:backgroundTint="#2196F3"
            app:useCompatPadding="true"/>
</RelativeLayout>

(Pic of the result on various API versions)


Now, let's get down to the fun part. How to make the layout a full screen layout and set the status bar color.

Let's jump right into code, try it out on your device and when we have confidence that it works,
then let's get down to understanding what is actually happening:

For lollipop devices:
activity.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }

(Check whether Color.TRANSPARENT works on lollipop)
(Test on below lollipop devices)

What is a Window?

What are these different flags you can apply to a window?

Whay is systemUiVisibility?

I think setStatusBarColor() needs no explanation as it is self explanatory.


Now, let's look at what we have got:

(Pic of the result on various API versions)

Hmmm, it looks weird on marshmallow and above devices. Why?

Problem on marshmallow and above devices:
The system bar icons are all white which may not look good if the general color scheme in your layout is light.

How to fix it?
systemUiVisibility to the rescue.
window.apply {
	            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
}

Cool..so, now our status bar looks pretty good on most of the API levels. But but.. what's up with this Floating Action Button. This guy is overlapping with the system bar icons and this kind of UI makes me as an user uncomfortable. So, let's fix this:

The actual content of your app layout needs to be shifted down so that it doesn't overlap. Ok, what do we need to shift it down? Margin..a marginTop will do. Right?
But now comes the million dollar question. How do we know how much marginTop do we need to give to the content view? There are many ways to do that but i'm going to go with the most definitive approach which has worked for me on a variety of devices.

Window insets to the rescue.

What are window insets?

How to get the insets of your current visible window?
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ride_activity_content_container)) { _, insets ->
    	Log.d("topInset", insets.systemWindowInsetTop)
    	Log.d("bottomInset", insets.getSystemWindowInsetBottom)
    	//and so on for left and right insets
        insets.consumeSystemWindowInsets()
    }

Why is insets.consumeSystemWindowInsets() required?

Now, it's just down to getting the view and setting a marginTop on it equal to the
topInset of the window:
(Why are we getting insets of the content container and not the entire view)
ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ride_activity_content_container)) { _, insets ->
        val menu = findViewById<FloatingActionButton>(R.id.fab_back_or_menu)
        val menuLayoutParams = menu.layoutParams as ViewGroup.MarginLayoutParams
        menuLayoutParams.setMargins(0, insets.systemWindowInsetTop, 0, 0)
        menu.layoutParams = menuLayoutParams
        insets.consumeSystemWindowInsets()
    }

Usually in a large application, things like these are repeated. So, wouldn't it be better to create a helpful kotlin extension on the Activity which we can call from any activity instead of repeating this code everywhere.


All the code can be found on this github link.


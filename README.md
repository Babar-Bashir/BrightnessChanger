# BrightnessChanger
I'm developing this application mainly for myself, because miniaml brightness of my device's lcd is very high so it drains my
eyes very fast. It requires root permission, so if you want to use it you must have a rooted device, otherwise you cannot
change the brightness. How does it work? It's very simple, there's a file called "/sys/class/leds/wled/brightness" and
as it's name says, it's responsible for brightness level, we can edit this file manually by writing value in range from 1 up to
255, but it takes quite a lot of time so I decided to simplify this process. If you have some suggestions how can I make this
application better (i.e. what interesting options you wish to add), please send me a message on my email: damian11131@gmail.com.
Application requires Android 4.1 or higher.

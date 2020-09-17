# Neurolab Android App

Repository of Android app for the Neurolab Open Hardware platform.

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/04c9c47bfb934605ab15394dd2f260be)](https://app.codacy.com/app/fossasia/neurolab-android?utm_source=github.com&utm_medium=referral&utm_content=fossasia/neurolab-android&utm_campaign=Badge_Grade_Settings)
[![Build Status](https://travis-ci.org/fossasia/neurolab-android.svg?branch=master)](https://travis-ci.org/fossasia/neurolab-android)
[![Gitter](https://badges.gitter.im/fossasia/neurolab.svg)](https://gitter.im/fossasia/neurolab?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![Mailing List](https://img.shields.io/badge/Mailing%20List-FOSSASIA-blue.svg)](https://groups.google.com/forum/#!forum/pslab-fossasia)
[![Twitter Follow](https://img.shields.io/twitter/follow/pslabio.svg?style=social&label=Follow&maxAge=2592000?style=flat-square)](https://twitter.com/pslabio)

This repository holds the Android app for the Neurolab Hardware. We are developing a neuro-device as a headband with integrated electronics. The goal of the Neurolab project is to create an easy to use open hardware measurement headset device for brain waves that can be plugged into an Android smartphone and a software application and enables us to understand our brains better.

## Buy

* You can get the device in future from the [FOSSASIA Shop](https://fossasia.com).
* More resellers will be listed on the [PSLab website](https://pslab.io/shop/).

## Communication

* The Neurolab [chat channel is on Gitter](https://gitter.im/fossasia/neurolab).
* Please also join us on the [Mailing List](https://groups.google.com/forum/#!forum/pslab-fossasia).

## Screenshots
<table>
        
  <tr>
  <td>
  <img src="/docs/images/start_screen_1.jpeg" align="top">
  
  One of the On-boarding screens of the app.
  </td>
  <td>
  <img src="/docs/images/home_screen.jpeg" align="top">
  
  Home and launcher screen of the app.
  </td>
  <td>
  <img src="/docs/images/relax.jpg" align="top">
  
  Relax mode - Relaxation screen of the app.
  </td>
  </tr>
  
  <tr>
  <td>
  <img src="/docs/images/focus_mode.png" align="top">

  Focus mode - Rocket game screen of the app.
  </td>
  <td>
  <img src="/docs/images/memory_graph_sc.jpeg" align="top">
  
  Memory Graph mode - Brain data visualization.
  </td>
  <td>
  <img src="/docs/images/pinlayout.jpeg" align="top">
  
  Pin layout screen of the app with pin descriptions.
  </td>
  </tr>

  <tr>
  <td>
  <img src="/docs/images/meditate.jpg" align="top">
  
  Meditation mode - Meditations screen of the app.
  </td>
  <td>
  <img src="/docs/images/about_screen.jpeg" align="top">
  
  About us page of the project in the app.
  </td>
  <td>
  <img src="/docs/images/settings_screen.jpeg" align="top">
  
  Settings screen of the app with a Developer mode.
  </td>
  </tr>
  
  <tr>
  <td>
  <img src="/docs/images/nav_menu.jpeg" align="top">

  
  Navigation drawer - Navigate to different app screens.
  </td>
  <td>
  <img src="/docs/images/statistics.jpeg" align="top">
  
  Statistics - Scores generated in Memory Graph mode.
  </td>
  <td>
  <img src="/docs/images/spectrum.png" align="top">
  
  Spectrum - Density spectrum of neuro data in Memory Graph mode.
  </td>
  </tr>
  </table>
  
## Goal

Our brains communicate through neurotransmitters and their activity emits electricity. The neuroheadset measures that electricity on the skin of the forehead and the software processes the signal so that it can be translated into a visual or auditory representation. The data that can be collected can be analyzed to identify mental health, stress, relaxation and even diseases like Alzheimer. 

Current devices in the medical industry are usually not accessible by doctors due to their high pricing. They are also complicated to use. The idea of the device is to integrate it into a headband and focus on signals that can be obtained through the frontal lobe.

A difference to existing projects like OpenBCI is that it will not be necessary to 3D print large headsets. Instead we are focusing on creating a device that collects as much data as possible through the forehead. To achieve this goal we are using high-grade sensors and flexible electronics.

## Features

Please check out the in-development features of the app like the Meditation mode, Bluetooth mode, etc. by enabling the Developer mode from the settings menu.

|   **Image**       |   **Feature**          | **Description**                               | **Status**         |
|-------------------|------------------------|-----------------------------------------------|--------------------|
| <img src = "/docs/images/home_screen.jpeg" width="200"/>  |  Launcher Screen  | The four major program modes: Focus, Relax, Memory Graph, Meditation, Every program mode is responsible for specific activities and games with your brain activity. | :heavy_check_mark: |
| <img src = "/docs/images/focus_mode.png" width="200"/>    |  Focus program Mode  | Focus program mode helps the users to increase their focus and concentration power by playing games. The rocket game comes with features like play, stop, record, program info, datalogger, seek to specific time, etc. | :heavy_check_mark: |
| <img src = "/docs/images/relax.jpg" width="200"/>    |  Relax program mode  | Relax program mode is intended to help users  to relax their mind and diminish their stress in life. | :soon: In Progress |
| <img src = "/docs/images/memory_graph_sc.jpeg" width="200"/>    |  Memory Graph program mode  | Memory Graph is a data visualization mode with the help of graphs, user friendly stats and a density spectrum. Users can import a dataset into the datalogger from where they can visualize it in the actual mode. This mode is implemented with features like play, stop, record, data logger, etc. | :heavy_check_mark: |
| <img src = "/docs/images/meditate.jpg" width="200"/>    |  Meditation program mode  | Meditation program mode helps the users with providing different categories of meditations which they can choose depending upon their mood. Every category has a list of meditations directed for that particular mood. | Development Mode |

## App workflow Videos

*   [NeuroLab App Overview](https://youtu.be/udXVOB4VPis)
*   [Device recording and file rename features](https://youtu.be/0jYBJDMOz_E)

## Dependencies

*   [Preference Fix Library](https://github.com/Gericop/Android-Support-Preference-V7-Fix)
*   [JSyn Library](http://www.softsynth.com/jsyn/beta/jsyn_on_android.php)
*   [Android About Page](https://github.com/medyo/android-about-page)
*   [Open Sound Control library](https://mvnrepository.com/artifact/com.illposed.osc/javaosc-core)
*   [Java Simple Serial Connector](https://mvnrepository.com/artifact/org.scream3r/jssc/2.8.0)
*   [JTransforms](https://mvnrepository.com/artifact/net.sourceforge.jtransforms/jtransforms/2.4.0)
*   [Java Open GL](https://mvnrepository.com/artifact/org.jogamp.jogl/jogl-all-main/2.3.2)
*   [Circle ImageView](https://github.com/hdodenhof/CircleImageView)
*   [MP Android Chart - Chart Library](https://github.com/PhilJay/MPAndroidChart)
*   [USB Serial Controller](https://github.com/felHR85/UsbSerial)
*   [App Intro](https://github.com/AppIntro/AppIntro)
*   [JFreeChart](https://mvnrepository.com/artifact/org.jfree/jfreechart/1.0.14)
*   [OpenCSV](https://mvnrepository.com/artifact/com.opencsv/opencsv/4.6)
*   [Gson](https://mvnrepository.com/artifact/com.google.code.gson/gson/2.8.5)

## Branch Policy

We have the following branches
* **development** All development goes on in this branch. If you're making a contribution, you are supposed to make a pull request to _development_. PRs to development branch must pass a build check and a unit-test check on Circle CI.
* **master** This contains shipped code. After significant features/bugfixes are accumulated on development, we make a version update and make a release.
* **apk** This branch contains two apk's, that are automatically generated on the merged pull request a) debug apk and b) release apk.

## Contributions Best Practices

Please help us follow the best practice to make it easy for the reviewer as well as the contributor. We want to focus on the code quality more than on managing pull request ethics.

* Single commit per pull request
* Reference the issue numbers in the commit message. Follow the pattern ``` Fixes #<issue number> <commit message>```
* Follow uniform design practices. The design language must be consistent throughout the app.
* The pull request will not get merged until and unless the commits are squashed. In case there are multiple commits on the PR, the commit author needs to squash them and not the maintainers cherrypicking and merging squashes.
* If the PR is related to any front end change, please attach relevant screenshots in the pull request description.
* Before you join development, please set up the project on your local machine, run it and go through the application completely. Press on any button you can find and see where it leads to. Explore.
* If you would like to work on an issue, drop in a comment at the issue. If it is already assigned to someone, but there is no sign of any work being done, please free to start working on it.

## Maintainers and Developers

*   Jaideep Prasad ([@jddeep](https://github.com/jddeep))
*   Mario Behling ([@mariobehling](http://github.com/mariobehling))
*   Padmal ([@CloudyPadmal](https://github.com/CloudyPadmal))

## License

This project is licensed under the GNU General Public License v3.0. A copy of [LICENSE](LICENSE) is to be present along with the source code. To obtain the software under a different license, please contact FOSSASIA.

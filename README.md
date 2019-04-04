# Neurolab Android App

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/04c9c47bfb934605ab15394dd2f260be)](https://app.codacy.com/app/fossasia/neurolab-android?utm_source=github.com&utm_medium=referral&utm_content=fossasia/neurolab-android&utm_campaign=Badge_Grade_Settings)
[![Build Status](https://travis-ci.org/fossasia/neurolab-android.svg?branch=master)](https://travis-ci.org/fossasia/neurolab-android)
[![Gitter](https://badges.gitter.im/fossasia/pslab.svg)](https://gitter.im/fossasia/pslab?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![Mailing List](https://img.shields.io/badge/Mailing%20List-FOSSASIA-blue.svg)](mailto:pslab-fossasia@googlegroups.com)
[![Twitter Follow](https://img.shields.io/twitter/follow/pslabio.svg?style=social&label=Follow&maxAge=2592000?style=flat-square)](https://twitter.com/pslabio)

This repository holds the Android app for the Neurolab Hardware. We are developing a neuro-device as a headband with integrated electronics.

The goal of the Neurolab project is to create an easy to use open hardware measurement headset device for brain waves that can be plugged into an Android smartphone and a software application and enables us to understand our brains better.

Our brains communicate through neurotransmitters and their activity emits electricity. The neuroheadset measures that electricity on the skin of the forehead and the software processes the signal so that it can translated into a visual or auditory representation. The data that can be collected can be analysed to identify mental health, stress, relaxation and even diseases like Alzheimer. 

Current devices in the medical industry are usually not accessible by doctors due to their high pricing. They are also complicated to use. The idea of the device is to integrate it into a headband and focus on signals that can be obtained through the frontal lobe.

A difference to existing projects like OpenBCI is that it will not be necessary to 3D print large headsets. Instead we are focusing on creating a device that collects as much data as possible through the forehead. To achieve this goal we are using high-grade sensors and flexible electronics.

## Screenshots
<table>
        <tr>
<td><img src = "https://user-images.githubusercontent.com/20669217/55556050-432cb580-5704-11e9-85dd-d223c16d7a38.png" height = "500" width="250"></td>
  </tr>
  </table>
  
  ## Contributions Best Practices

### For first time Contributors

First time contributors can read [CONTRIBUTING.md](/CONTRIBUTING.md) file for help regarding creating issues and sending pull requests.

### Branch Policy

We have the following branches
 * **development** All development goes on in this branch. If you're making a contribution, you are supposed to make a pull request to _development_. PRs to development branch must pass a build check and a unit-test check on Circle CI.
 * **master** This contains shipped code. After significant features/bugfixes are accumulated on development, we make a version update and make a release.
 * **apk** This branch contains two apk's, that are automatically generated on the merged pull request a) debug apk and b) release apk.
 * Please download and test the app that is using the code from the development and master branches [here](https://github.com/fossasia/neurolab-android/tree/apk).
 
### Code practices

Please help us follow the best practices to make it easy for the reviewer as well as the contributor. We want to focus on the code quality more than on managing pull request ethics.
 * Single commit per pull request
 * For writing commit messages please read the COMMITSTYLE carefully. Kindly adhere to the guidelines.
 * Follow uniform design practices. The design language must be consistent throughout the app.
 * The pull request will not get merged until and unless the commits are squashed. In case there are multiple commits on the PR, the commit author needs to squash them and not the maintainers cherrypicking and merging squashes.
 * If the PR is related to any front end change, please attach relevant screenshots in the pull request description.

### Join the development

 * Before you join development, please set up the project on your local machine, run it and go through the application completely. Press on any button you can find and see where it leads to. Explore. (Don't worry ... Nothing will happen to the app or to you due to the exploring :wink: Only thing that will happen is, you'll be more familiar with what is where     and might even get some cool ideas on how to improve various aspects of the app.)
 * Also please set up the [neurolab-desktop](https://github.com/fossasia/neurolab-desktop) project to your local machine in IntelliJ and explore it, as we are developing this android application based on the desktop-application.
 * If you would like to work on an issue, drop in a comment at the issue. If it is already assigned to someone, but there is no sign of any work being done, please free to drop in a comment so that the issue can be assigned to you if the previous assignee has dropped it entirely.

## For Testers: Testing the App
If you are a tester and want to test the app, you have two ways to do that:
1. **Installing APK on your device:** You can get debug APK as well as Release APK in apk branch of the repository. After each PR merge, both the APKs are automatically updated. So, just download the APK you want and install it on your device. The APKs will always be the latest one.
  
## External Depenedencies
*preference fix library* : com.takisoft.fix:preference-v7:$rootProject.prefFixLibraryVersion"

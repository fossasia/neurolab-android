## Summary 
EEG data from 10 students watching MOOC videos.

## Description
This dataset consists of collected EEG signal data from 10 college students while they watched MOOC video clips. Videos of two types were used:
1. Online education videos that are assumed not to be confusing for college students, such as videos of the introduction of basic algebra or geometry.
2. Videos that are expected to confuse a typical college student if a student is not familiar with the video topics like Quantum Mechanics, and Stem Cell Research. 

Also take a note of the following points ->
*   **Video Details**: 20 videos were prepared, 10 in each category. Each video is about 2 minutes long. A two-minute clip in the middle of a topic was chopped to make the videos more confusing. 
*   **Hardware**: The students wore a single-channel wireless MindSet that measured activity over the frontal lobe. The MindSet measures the voltage between an electrode resting on the forehead and two electrodes (one ground and one reference) each in contact with an ear. 
*   **User Input for Mapping**: After each session, the student rated his/her confusion level on a scale of 1-7, where one corresponded to the least confusing and seven corresponded to the most confusing. These labels if further normalized into labels of whether the students are confused or not. This label is offered as self-labelled confusion in addition to our predefined label of confusion.

## Content
These data are collected from ten students, each watching ten videos. Therefore, it can be seen as only 100 data points for these 12000+ rows. If you look at this way, then each data point consists of 120+ rows, which is sampled every 0.5 seconds (so each data point is a one minute video). Signals with higher frequency are reported as the mean value during each 0.5 second.

**EEG_Dataset.csv**: Contains the EEG data recorded from 10 students in the following format.

|SubjectID|Age|Ethnicity|Gender|VideoID|Attention|Mediation|Raw|Delta|Theta|Alpha1|Alpha2|Beta1|Beta2|Gamma1|Gamma2|predefinedlabel|user-definedlabeln|
|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|--|

**video data**  : Each video lasts roughly two-minute long, we remove the first 30 seconds and last 30 seconds, only collect the EEG data during the middle 1 minute. 

## Size
Videos - 110 MB
CSV file - 5 MB

## Sample MOOC videos
You can download the videos [here](https://drive.google.com/open?id=1vwv1yCsg8h2wgZbuotn5gDD7gD2BkIKh).

## Credits
Thanks to the work and efforts carried out by Haohan Wang. More details linked [here](https://www.kaggle.com/wanghaohan/confused-eeg).

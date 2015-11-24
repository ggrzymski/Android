Designed the application based off of the Android KitKat platform. 

Implemented a single-user model, using session persistence.
Application features included:

1. Maintaining a database of different users through serialization. 

2. Creating/Deleting/Renaming album folders.

3. Adding/Deleting/Moving/Renaming photo image files.

4. Adding/Deleting tag and caption descriptions for individual images.

5. Slideshow gallery of all images within an album.

6. Search for photos by tag descriptions.

Please use an emulator that has minimum SDK version set to 14, and it also  has SD card support enabled with a minimum storage of 64 MB. 

Primary testing was performed on the HVGA.4.4.2 3.2" HVGA slider(ADP1) (320 x 480:mdpi)

Target: Android 4.4.2 - API Level 19

CPU/ABI: ARM (armeabi-v7a)

RAM: 512 

VM Heap: 16

Internal Storage: 200

All tested image files were pushed into the storage/sdcard/DCIM folder via DDMS.
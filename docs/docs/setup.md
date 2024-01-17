# Setup

## Building the application

There are two options to get FileSilo:

- Load the NTF template from the repository and create a new HCL Notes application with it. You can find the current template in the folder `ntf` of this repository.
- clone the repository and build your own NSF from the ODP in the repository using Domino Designer

## ACL

The recommended ACL settings are:

| User/Group                   | Type         | Level   | Roles              |
| ---------------------------- | ------------ | ------- | ------------------ |
| Anonymous                    | Undefined    | Reader  | [Upload]           |
| LocalDomainAdmins            | Person Group | Manager | [Admin], [Profile] |
| (Other Users/Groups/Default) | -            | Editor  | -                  |

The important role is `[Upload]`. This ensures anonymous users to upload files even though they are readers to the application. Admins should control the general settings (profile) to define options.

![](images/acl.png)

## Login

Click the link in the upper-right menu to login to Domino. If you are recognized as a user with the role `[Profile]`, you can also see an additional menu entry here called `Settings`.

## Settings

Settings are available only for logged-in users with the role `[Profile]`. To open the settings just click on the upper-right menu item which displays your user name and select `Settings`.

This will bring up this page:

![](images/settings_readonly.png)

The settings page has two tabs:

- Settings
- Pushover

### Settings

### Pushover
## 0.5.9
#### new features

#### improvements

#### fixes
* Set CalendarURL to null when it's not in the response

## 0.5.8
#### new features

#### improvements

#### fixes
* fixed version check

## 0.5.7
#### new features

#### improvements
* Show notification while downloading file
* Show error message when server returns invalid resonses (Login screen)
* Follow URL redirects (HTTP codes 301, 302, 307, 308)
* Automatically change the server URL to the correct endpoint if possible
* Show error message when all swimlanes are deactivated
* Moved new comment button to listview
* Moved new subtask button to listview

#### fixes
* fixed crash when user has assigned tasks but no projects
* fixed crash when trying to change swimlane/column while receiving data
* fixed crash when trying to add subtask/comment while receiving data
* handle deactivated swimlanes correctly
* Various bug fixes

## 0.5.6
#### new features
* Download files attached to tasks
* Move task to other columns
* Move task to other swimlane

#### improvements
* sort task by position value

#### fixes
* Various bug fixes

## 0.5.5
#### new features
* Support for Android 4.2
* Rudimentary support for task attachments

#### improvements
* replaced floating action button with the default option menu to support smaller layouts

#### fixes
* Various bug fixes

## 0.5.4
#### new features

#### improvements
* Support for self-signed certificates (when imported into Android)
* Clickable links in project and task descriptions

#### fixes
* Wrong login credentials result in kanboard version error
* Allow login on servers that run a development version of Kanboard

## 0.5.3
#### new features

#### improvements
* japanese translation (thanks to naofum)

#### fixes
* App is crashing when user has no projects
* HTTP redirects should be handled correctly
* Task due date can now be deleted

## v0.5.2
#### new features

#### improvements
* Spanish translation (thanks to Esteban Monge)

## v0.5.1
#### new features

#### improvements
* Support for Kanboard v1.0.41
* Show version number in about activity

#### fixes
* fixed null description in default swimlane
* fixed bug that crashed task view

## v0.5
#### new features
* Basic UI to add and modify tasks

#### improvements
* Support for HTTP connections to host
* Refresh view when a task was edited
* Check Kanboard version on login

#### fixes
* Task owner might show null sometimes
* Wrong text on close task button

## v0.4.3
#### fixes
* Crash if project description is empty (issue #2)

## v0.4.2
#### improvements
* Send crash reports via email


## v0.4.1
#### improvements
* Redesigned subtask list to solve layout problems
* Better looking launcher icon in Android 6 and lower

#### fixes
* Last elements of long subtask lists could be hidden

## v0.4
#### new features
* Confirmation dialog for delete actions
* New design for comments entries with markdown support
* New design for subtask entries with time tracking

#### improvements
* Show category in task lists (only for projects not for dashboard)
* Markdown support

#### fixes
* Show string when task is not assigned to a user
* Show string when category is not assigned
* Login activity is started twice on first run
* First subtask is always checked
* Closed tasks are mixed with open tasks
* Start and due dates of tasks are invisible
* Empty screen when pressing refresh

## v0.3.1
#### fixes
* White text in Android versions below 7

## v0.3
#### new features
* Remove task from FAB in task view
* Landscape layout for task view

#### improvements
* New icon for FAB menu
* Color of FAB matches Actionbar
* Show error messages in Snackbars
* Improved network speed
* Refresh button in top action bar
* New Icons

#### fixes
* Read HTTP error stream correctly

## v0.2
#### new features
* FAB menu in Task Details
* Create, edit and remove task comments (in tasks details)
* Open and close tasks (in tasks details)
* Create, edit and remove subtasks (in tasks details)

#### improvements
* New information text in about activity

#### fixes

## v0.1
* First release
* View Dashboard
* View Project Details and Tasks
* View Task Details

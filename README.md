# ToDoList

## About
This is the repository for our COSC310 project. We are creating an Android to-do list app for students.

## Class structure
All layout code is stored in the layout folder as six xml files.
- activity_main.xml contains the layout for our main screen, including a RecyclerView for to-do items
- todo_item.xml is the layout of an individual to-do item, which are used to populate the RecyclerView
- activity_edit_todo.xml contains the layout for the edit to-do page that users can reach through the context menu after clicking on a to-do
- activity_edit_tag.xml contains the layout for the edit tag screen that users can reach through the context menu after clicking on a to-do
- tag_item.xml is the layout of an individual tag item
- filter_menu_item.xml contains the layout of an individual filter item

All functional code is contained in .java files in the todolist folder.

**MainActivity** controls the main screen. By default, it extends AppCompatActivity, and it extends
our custom **ToDoClickListener**. The onCreate() method contains initialization code. 
New to-do's are created from user input using the createToDo method.
The onEditClick() and onCheckClick() methods are bound to the RecyclerView elements (individual to-dos).

**EditToDoActivity** controls the edit to-do page. Users can navigate to this page by clicking on a to-do.
This triggers the onEditClick() method and sends the to-do array list to the **EditToDoActivity** class.
On the edit-to page, users can change the text of their to-do, and set a due date.

**ToDo** is a serializable class to store information about to-dos.

**ToDoAdapter** is a custom RecyclerViewAdapter. It contains an inner class, **MyViewHolder**, which 
is a custom ViewHolder.

**ToDoClickListener** is an interface implemented by the **MainActivity**. 
It contains the onEditClick() and onCheckClick() method.

**FilterPowerMenuItem** defines each item in a PowerMenu for the filter menu on the main activity.

**EditTagActivity** controls the edit tag page. Users can navigate to this page through the context 
menu that pops up when clicking on the three dots on a task. There, they can add new or existing 
tags to tasks. They can also delete existing tags.

**FilterMenuAdapter** is a custom MenuBaseAdapter. 

**TagAdapter** is a custom RecyclerViewAdapter. It contains an inner class, **MyViewHolder**, which 
is a custom ViewHolder.

**TagClickListener** is an interface implemented by the **AddTagActivity**.
It contains the onDeleteClick() method.

Test code is contained in **EditToDoActivityUnitTest**

## Documentation for Assignment 3

Since our system already had a GUI, we made improvements to the existing GUI. We designed a custom icon and added that to our app. We also implemented general functionality and interface improvements, such as adding a search bar.

### Features that enable your system to handle the drawbacks of the first version of the system

#### Made to-do's searchable - Limitation fix 1
- users can search for tasks by name. This helps users find tasks more easily. Used the AndroidX library to implement a search bar. This feature improves the UI and laid the groundwork for allowing users to search tasks.
<img src="https://user-images.githubusercontent.com/77898527/200717087-9fd058f9-d2d8-4784-ab4d-d55fc04206fd.png" alt="searchTasks" width="100"/>

#### Completed tasks are viewable - Limitation fix 2
- now displaying completed tasks using a RecyclerView (from AndroidX Library). This feature is a major improvement, because it allows users to reference previously completed tasks. Previously, tasks just became invisible to the user upon completion.
<img src="https://user-images.githubusercontent.com/77898527/200717214-a077b894-7877-4953-9e43-907a2344fec9.png" alt="viewCompleted" width="100"/>

#### Sorting tasks by their due date - Limitation fix 3
- Users are able to sort their tasks by due date. They can sort them by due date ascending or descending.

#### TBD - Limitation fix 4 ⚠️⚠️⚠️⚠️⚠️

#### Added filtering by tags
- users can sort tasks based on tags. This feature helps users organize their tasks.
<img src="https://user-images.githubusercontent.com/77898527/200717120-6b2f2f3e-4b08-4d8b-b7b9-7c01065b5a02.png" alt="filteringByTags" width="100"/>

#### Improved goBack()
- refactored the method to improve performance (a previous limitation). This fix improves performance and long-term stability.

#### Show due date on main page
- users can now see the due dates they add on the main page. This feature makes the app easier to use; users can see the due date on the main page instead of having to click on the task now.
<img src="https://user-images.githubusercontent.com/77898527/200717154-85eb8992-f97d-4f5e-9f8c-692e3994d65a.png" alt="displayDueDate" width="100"/>

#### Refactored the way persistence is implemented
- Changed the way that tasks are stored to the disk. There is only one list of tasks being saved now. This makes the code easier to work with because we don't need to always be checking multiple lists when performing operations. This still is a problem to an extent, but is easier to manage.

#### Changed list behaviour on adding task
- The list now automatically scrolls to the bottom to show newly added tasks. This makes the app easier to use because users can see and edit their tasks immediately after adding them instead of having to scroll down.

#### Added button to conveniently navigate the list
- Users can use the gray button on the right to quickly navigate between the top and bottom of the list. This feature improves the efficiency of use for our app. Users can now easily navigate to their recent tasks at the bottom and back up to the search bar and the filter menu.

### Features that use open-source libraries to improve the functionality of the system

#### Added tags - New feature 1
- users can now classify their tasks using tags. Tags are implemented using, among others the AndroidX Library. Tags improve our system because they give the user the option to categorize their tasks. We used a library called RecyclerView for implenting the list of active tags on a task. This one of the libraries available in the Android library, but it was a steep learning curve to be able use it. Many hours were spent by multiple team members in order to understand how to use RecyclerView.
<img src="https://user-images.githubusercontent.com/77898527/200717171-08738099-3592-425b-9bb1-6eaeb21d4fd5.png" alt="addTags" width="100"/>

#### Added context menu - New feature 2
- used the Skydoves open-source library to add a context menu when the user clicks the three dots on a task. This feature improves the effectiveness of our app by reducing the number of steps users have to complete to perform common tasks.
<img src="https://user-images.githubusercontent.com/77898527/200717248-8847e1d4-6115-4310-9fd0-512885bbe4ea.png" alt="contextMenu" width="100"/>

#### App notifications and email notifications - New feature 3
- ⚠️⚠️⚠️⚠️⚠️ Need to add info here ⚠️⚠️⚠️⚠️⚠️

## Compiling our code
There are two ways to compile our code:
1) clone the project in Android studio and run it on an emulator in the app
2) use the [apk file](https://github.com/COSC310-Team12/ToDoList/releases/download/v0.1.0/ToDoList-v0.1.0.apk) from our latest [release](https://github.com/COSC310-Team12/ToDoList/releases) to install the app on an Android phone


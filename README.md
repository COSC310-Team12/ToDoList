# ToDoList

## About
This is the repository for our COSC310 project. We are creating an Android to-do list app for students.

## Class structure
All layout code is stored in the layout folder as three xml files.
- activity_main.xml contains the layout for our main screen, including a RecyclerView for to-do items
- todo_item.xml is the layout of an individual to-do item, which are used to populate the RecyclerView
- activity_edit_todo.xml contains the layout for the edit to-do page that users can reach by clicking on a to-do.

All functional code is contained in .java files in the todolist folder.

**MainActivity** controls the main screen. By default, it extends AppCompatActivity, and it extends
our custom **ToDoClickListener**. The onCreate() method contains initialization code. 
New to-do's are created from user input using the createToDo method.
The onEditClick() and onCheckClick() methods are bound to the RecyclerView elements (individual to-dos).

**EditToDo** controls the edit to-do page. Users can navigate to this page by clicking on a to-do.
This triggers the onEditClick() method and sends the to-do array list to the **EditToDo** class.
On the edit-to page, users can change the text of their to-do, and set a due date.

**ToDo** is a serializable class to store information about to-dos.

**ToDoAdapter** is a custom RecyclerViewAdapter. It contains an inner class, **MyViewHolder**, which is a
custom ViewHolder.

**ToDoClickListener** is an interface implemented by the **MainActivity**. 
It contains the onEditClick() and onCheckClick() method.

## Compiling our code
There are two ways to compile our code:
1) clone the project in Android studio and run it on an emulator in the app
2) use the [apk file](https://github.com/COSC310-Team12/ToDoList/releases/download/v0.1.0/ToDoList-v0.1.0.apk) from our latest [release](https://github.com/COSC310-Team12/ToDoList/releases) to install the app on an Android phone


Began as an assignment for Operating Systems to create a representation of memory allocation and storage using multiple different fit algorithms
Language: Java
Run project from MemfitGUI.java
Fit Algorithms:
First, Next, Worst, Best, Random

User creates the initial pool with the desired units of storage and specifies the desired fit algorithm.
Example: pool next 1000

Then the user can choose to allocate or free blocks of specified  name and size.
Example:
alloc A 100
alloc B 50
free A
alloc C 150
free C

Program prints number of failed allocations, percentage free and used of data pool
package com.example.dsamaster.core.seed

import com.example.dsamaster.core.database.CodingProblemEntity
import com.example.dsamaster.core.database.DeckEntity
import com.example.dsamaster.core.database.FlashcardEntity
import com.example.dsamaster.core.database.LessonEntity
import com.example.dsamaster.core.database.PatternEntity
import com.example.dsamaster.core.database.QuizEntity
import com.example.dsamaster.core.database.QuizQuestionEntity
import com.example.dsamaster.core.database.TopicEntity

/** Built-in learning content. Adding more content is a data-only change here. */
object SeedData {

    val topics = listOf(
        TopicEntity(1, "arrays", "Arrays", "Contiguous memory, O(1) index access",
            "An array stores elements next to each other in memory, so any index can be read in constant time. Insertion and deletion in the middle require shifting elements.",
            "A row of numbered lockers: you can open locker 42 instantly, but squeezing a new locker into the middle means moving every locker after it.", 1),
        TopicEntity(2, "strings", "Strings", "Immutable character sequences",
            "Strings are arrays of characters with rich library support. In Kotlin they are immutable, so building strings in a loop should use StringBuilder.",
            "A printed sentence: to change one word you have to reprint the page — unless you use a whiteboard (StringBuilder).", 2),
        TopicEntity(3, "linked-lists", "Linked Lists", "Nodes chained by references",
            "Each node holds a value and a reference to the next node. Insertion and deletion at a known node are O(1), but access by index is O(n).",
            "A treasure hunt where every clue points to the next location — fast to add a new clue, slow to reach clue #500.", 3),
        TopicEntity(4, "stacks", "Stacks", "Last in, first out (LIFO)",
            "A stack supports push, pop and peek, all O(1). It powers function calls, undo, and bracket matching.",
            "A stack of plates: you can only take the plate on top.", 4),
        TopicEntity(5, "queues", "Queues", "First in, first out (FIFO)",
            "A queue supports enqueue at the back and dequeue at the front, both O(1) with a linked list or ring buffer. Used in BFS and schedulers.",
            "A line at a ticket counter: first person in line is served first.", 5),
        TopicEntity(6, "hash-tables", "Hash Tables", "Average O(1) lookup by key",
            "A hash function maps keys to buckets. Lookups, inserts and deletes are O(1) on average, O(n) worst case with many collisions.",
            "A library catalog: instead of scanning every shelf, the catalog tells you exactly which shelf a book is on.", 6),
        TopicEntity(7, "recursion", "Recursion", "Functions that call themselves",
            "A recursive function solves a problem by solving smaller instances of itself, with a base case to stop. Every recursion uses the call stack.",
            "Russian nesting dolls: to open the whole set you open a doll, then repeat on the smaller doll inside.", 7),
        TopicEntity(8, "searching", "Searching", "Linear and binary search",
            "Linear search scans every element in O(n). Binary search on sorted data halves the range each step, giving O(log n).",
            "Finding a word in a dictionary: you don't read from page one — you open the middle and jump left or right.", 8),
        TopicEntity(9, "sorting", "Sorting", "Ordering data efficiently",
            "Comparison sorts range from O(n^2) (bubble, insertion) to O(n log n) (merge, quick, heap). Stability and memory use also matter.",
            "Sorting a hand of cards: insertion sort is exactly how most people arrange cards one at a time.", 9),
        TopicEntity(10, "trees", "Trees", "Hierarchical node structures",
            "A tree has a root and children with no cycles. Traversals: preorder, inorder, postorder (DFS) and level order (BFS).",
            "A company org chart: one CEO at the root, departments branching below.", 10),
        TopicEntity(11, "bst", "Binary Search Trees", "Ordered trees, O(log n) when balanced",
            "In a BST, left subtree < node < right subtree. Search, insert and delete are O(h); balanced trees keep h = log n.",
            "A well-run tournament bracket: each comparison eliminates half the remaining players.", 11),
        TopicEntity(12, "heaps", "Heaps", "Priority queues in an array",
            "A binary heap keeps the min (or max) at the root; insert and remove are O(log n). Stored compactly in an array.",
            "A hospital ER: not first-come-first-served — the most urgent patient is always treated next.", 12),
        TopicEntity(13, "graphs", "Graphs", "Vertices and edges",
            "Graphs model networks. Represent with adjacency lists (sparse) or matrices (dense). Core algorithms: BFS, DFS, Dijkstra, topological sort.",
            "A city map: intersections are vertices, roads are edges — some one-way, some with tolls (weights).", 13),
        TopicEntity(14, "greedy", "Greedy Algorithms", "Locally optimal choices",
            "Greedy algorithms pick the best-looking option at each step. They are fast and simple but only correct when the greedy-choice property holds.",
            "Making change with the largest coins first — works for standard coin systems, fails for unusual ones.", 14),
        TopicEntity(15, "backtracking", "Backtracking", "Explore, then undo",
            "Backtracking tries a choice, recurses, and undoes it if it leads to a dead end. Used for permutations, N-Queens, Sudoku.",
            "Solving a maze: walk a path, and when you hit a wall, retrace your steps to the last junction.", 15),
        TopicEntity(16, "dp", "Dynamic Programming", "Remember solved subproblems",
            "DP applies when a problem has overlapping subproblems and optimal substructure. Memoize top-down or tabulate bottom-up.",
            "Climbing stairs and writing the number of ways on each step so you never recount from scratch.", 16),
        TopicEntity(17, "tries", "Tries", "Prefix trees for strings",
            "A trie stores strings character by character; lookup and insert are O(length of word). Great for autocomplete and prefix queries.",
            "A phone contact list that narrows as you type each letter.", 17),
        TopicEntity(18, "bits", "Bit Manipulation", "Working directly with binary",
            "AND, OR, XOR and shifts enable compact tricks: checking parity, isolating the lowest set bit, and constant-space solutions.",
            "Light switches on a panel: each bit is a switch you can flip, test, or combine.", 18),
    )

    val lessons = listOf(
        LessonEntity(101, 1, "Array fundamentals", 1, """
# What is an array?
An array is a fixed-size block of contiguous memory. The address of element i is computed directly, which is why reading arr[i] is O(1).
> Key insight: arrays trade flexible size for instant index access.
# Common operations
| Operation | Time |
| Access by index | O(1) |
| Search (unsorted) | O(n) |
| Insert/delete at end | O(1) amortized |
| Insert/delete in middle | O(n) |
# Kotlin examples
```
val nums = intArrayOf(3, 1, 4, 1, 5)
val list = mutableListOf(3, 1, 4)   // resizable, backed by an array
list.add(1)                          // amortized O(1)
list.add(0, 9)                       // O(n): shifts everything right
```
# Common mistakes
- Off-by-one errors at index 0 and size - 1.
- Using List when IntArray avoids boxing in hot loops.
- Forgetting that removing from the middle shifts all later elements.
""".trimIndent()),
        LessonEntity(102, 1, "Two pointers and sliding window", 2, """
# Two pointers
Many array problems become O(n) with two indices moving toward each other or in the same direction.
```
fun isPalindrome(s: String): Boolean {
    var i = 0; var j = s.length - 1
    while (i < j) {
        if (s[i] != s[j]) return false
        i++; j--
    }
    return true
}
```
# Sliding window
Keep a window [left, right] and maintain an invariant while expanding and shrinking it.
- Fixed window: sums or averages of size k.
- Variable window: longest substring without repeats.
> Interview tip: if a problem says "contiguous subarray", think sliding window first.
""".trimIndent()),
        LessonEntity(103, 2, "Strings in Kotlin", 1, """
# Immutability
Kotlin strings cannot change. Concatenating in a loop copies the whole string each time — O(n^2) total.
```
val sb = StringBuilder()
for (word in words) sb.append(word)
val result = sb.toString()   // O(n) total
```
# Useful tools
- s.reversed(), s.sorted() via toCharArray()
- Frequency counting with an IntArray(26) for lowercase letters
| Operation | Time |
| charAt / s[i] | O(1) |
| substring | O(n) |
| concatenation | O(n) |
# Common mistakes
- Comparing with == is fine in Kotlin (structural), unlike Java's reference ==.
- Building strings with + inside loops.
""".trimIndent()),
        LessonEntity(104, 3, "Singly linked lists", 1, """
# Structure
Each node stores a value and a next reference. The list is reached through its head.
```
class ListNode(var value: Int) {
    var next: ListNode? = null
}
```
| Operation | Time |
| Access by index | O(n) |
| Insert/delete at head | O(1) |
| Insert/delete after a known node | O(1) |
# The classic: reverse a list
```
fun reverse(head: ListNode?): ListNode? {
    var prev: ListNode? = null
    var curr = head
    while (curr != null) {
        val next = curr.next
        curr.next = prev
        prev = curr
        curr = next
    }
    return prev
}
```
# Common mistakes
- Losing the rest of the list by overwriting next before saving it.
- Forgetting to handle the empty list and single-node cases.
- Not using a dummy head node when the head itself may change.
""".trimIndent()),
        LessonEntity(105, 3, "Fast and slow pointers", 2, """
# The technique
Two pointers move at different speeds. When the fast one moves two steps per iteration, useful facts emerge.
- Middle of list: when fast reaches the end, slow is at the middle.
- Cycle detection (Floyd): if there is a cycle, fast and slow must meet.
```
fun hasCycle(head: ListNode?): Boolean {
    var slow = head
    var fast = head
    while (fast?.next != null) {
        slow = slow?.next
        fast = fast.next?.next
        if (slow === fast) return true
    }
    return false
}
```
> Note the identity check ===: we care about the same node, not equal values.
""".trimIndent()),
        LessonEntity(106, 4, "Stacks and their uses", 1, """
# LIFO in practice
Push, pop and peek are all O(1). In Kotlin use ArrayDeque.
```
val stack = ArrayDeque<Char>()
stack.addLast('(')          // push
val top = stack.last()      // peek
stack.removeLast()          // pop
```
# Valid parentheses — the canonical stack problem
```
fun isValid(s: String): Boolean {
    val stack = ArrayDeque<Char>()
    val pairs = mapOf(')' to '(', ']' to '[', '}' to '{')
    for (c in s) {
        if (c in pairs.values) stack.addLast(c)
        else if (stack.isEmpty() || stack.removeLast() != pairs[c]) return false
    }
    return stack.isEmpty()
}
```
# Where stacks appear
- Function call stack and recursion
- Undo/redo, browser history
- Monotonic stacks for "next greater element" problems
""".trimIndent()),
        LessonEntity(107, 5, "Queues and deques", 1, """
# FIFO in practice
Enqueue at the back, dequeue at the front, both O(1) with ArrayDeque.
```
val queue = ArrayDeque<Int>()
queue.addLast(1)             // enqueue
val front = queue.first()    // peek
queue.removeFirst()          // dequeue
```
# Why queues matter
- Breadth-first search visits nodes level by level using a queue.
- Task schedulers and buffering are queues.
- A deque (double-ended queue) supports O(1) at both ends — used in sliding-window maximum.
| Structure | Front ops | Back ops |
| Queue | O(1) remove | O(1) add |
| Deque | O(1) both | O(1) both |
""".trimIndent()),
        LessonEntity(108, 6, "Hash maps and sets", 1, """
# How hashing works
A hash function turns a key into a bucket index. Good hash functions spread keys evenly; collisions are handled by chaining or open addressing.
| Operation | Average | Worst |
| get / put / remove | O(1) | O(n) |
# Kotlin usage
```
val counts = HashMap<Char, Int>()
for (c in "hello") counts[c] = (counts[c] ?: 0) + 1

val seen = HashSet<Int>()
fun hasDuplicate(nums: IntArray) = nums.any { !seen.add(it) }
```
# Classic pattern: Two Sum
Store each number's index as you scan; check if target - current was already seen. One pass, O(n).
# Common mistakes
- Mutating an object after using it as a key.
- Assuming iteration order (use LinkedHashMap if order matters).
""".trimIndent()),
        LessonEntity(109, 7, "Thinking recursively", 1, """
# The two ingredients
- Base case: the smallest input answered directly.
- Recursive case: reduce the problem and trust the function on the smaller input.
```
fun factorial(n: Int): Long =
    if (n <= 1) 1L else n * factorial(n - 1)
```
# The call stack
Each call adds a frame; deep recursion can overflow the stack. Iteration or tail recursion (tailrec in Kotlin) avoids this.
```
tailrec fun gcd(a: Int, b: Int): Int =
    if (b == 0) a else gcd(b, a % b)
```
# Common mistakes
- Missing or unreachable base case (infinite recursion).
- Recomputing the same subproblem — the bridge to dynamic programming.
""".trimIndent()),
        LessonEntity(110, 8, "Binary search", 1, """
# The idea
On sorted data, compare with the middle and discard half. O(log n).
```
fun binarySearch(nums: IntArray, target: Int): Int {
    var lo = 0
    var hi = nums.size - 1
    while (lo <= hi) {
        val mid = lo + (hi - lo) / 2
        when {
            nums[mid] == target -> return mid
            nums[mid] < target -> lo = mid + 1
            else -> hi = mid - 1
        }
    }
    return -1
}
```
> mid = lo + (hi - lo) / 2 avoids integer overflow — a classic interview detail.
# Beyond exact match
- Lower bound: first index with value >= target.
- Binary search on the answer: minimize a feasible value (e.g., smallest capacity that works).
# Common mistakes
- Infinite loops from wrong lo/hi updates.
- Applying binary search to unsorted data.
""".trimIndent()),
        LessonEntity(111, 9, "Sorting algorithms", 1, """
# The landscape
| Algorithm | Time | Space | Stable |
| Insertion sort | O(n^2) | O(1) | Yes |
| Merge sort | O(n log n) | O(n) | Yes |
| Quick sort | O(n log n) avg | O(log n) | No |
| Heap sort | O(n log n) | O(1) | No |
# Merge sort in Kotlin
```
fun mergeSort(a: IntArray): IntArray {
    if (a.size <= 1) return a
    val mid = a.size / 2
    val left = mergeSort(a.copyOfRange(0, mid))
    val right = mergeSort(a.copyOfRange(mid, a.size))
    val out = IntArray(a.size)
    var i = 0; var j = 0; var k = 0
    while (i < left.size && j < right.size)
        out[k++] = if (left[i] <= right[j]) left[i++] else right[j++]
    while (i < left.size) out[k++] = left[i++]
    while (j < right.size) out[k++] = right[j++]
    return out
}
```
> In practice, call sorted() / sort() — but interviews expect you to implement one O(n log n) sort.
""".trimIndent()),
        LessonEntity(112, 10, "Trees and traversals", 1, """
# Vocabulary
Root, parent, child, leaf, height, depth. A binary tree has at most two children per node.
```
class TreeNode(var value: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}
```
# Traversals
- Preorder: node, left, right (copying trees)
- Inorder: left, node, right (sorted order in a BST)
- Postorder: left, right, node (deleting trees)
- Level order: BFS with a queue
```
fun inorder(node: TreeNode?, out: MutableList<Int>) {
    if (node == null) return
    inorder(node.left, out)
    out.add(node.value)
    inorder(node.right, out)
}
```
| Traversal | Time | Space |
| Any DFS | O(n) | O(h) |
| Level order | O(n) | O(w) |
""".trimIndent()),
        LessonEntity(113, 11, "BST operations", 1, """
# The invariant
For every node: everything in the left subtree is smaller, everything in the right subtree is larger.
```
fun search(node: TreeNode?, target: Int): TreeNode? = when {
    node == null || node.value == target -> node
    target < node.value -> search(node.left, target)
    else -> search(node.right, target)
}
```
| Operation | Balanced | Degenerate |
| Search / insert / delete | O(log n) | O(n) |
> A BST built from sorted input becomes a linked list — this is why self-balancing trees (AVL, Red-Black) exist.
# Validate a BST
Track an allowed (min, max) range while recursing; each step tightens the range. Checking only parent vs child is a famous wrong answer.
""".trimIndent()),
        LessonEntity(114, 12, "Binary heaps", 1, """
# Array-backed trees
A heap is a complete binary tree stored in an array: children of index i live at 2i+1 and 2i+2. The root is always the minimum (min-heap) or maximum (max-heap).
| Operation | Time |
| peek min/max | O(1) |
| insert | O(log n) |
| remove root | O(log n) |
| build heap from array | O(n) |
# Kotlin usage
```
import java.util.PriorityQueue
val minHeap = PriorityQueue<Int>()
minHeap.add(5); minHeap.add(1); minHeap.add(3)
val smallest = minHeap.poll()   // 1
val maxHeap = PriorityQueue<Int>(compareByDescending { it })
```
# Classic use: top-K elements
Keep a min-heap of size K while streaming values — O(n log k), far better than sorting everything.
""".trimIndent()),
        LessonEntity(115, 13, "Graph representations and BFS/DFS", 1, """
# Representing graphs
- Adjacency list: Map or Array of neighbor lists — O(V + E) space, best for sparse graphs.
- Adjacency matrix: O(V^2) space, O(1) edge lookup.
```
val graph = Array(n) { mutableListOf<Int>() }
graph[0].add(1)   // edge 0 -> 1
```
# BFS — shortest path in unweighted graphs
```
fun bfs(start: Int, graph: Array<MutableList<Int>>): IntArray {
    val dist = IntArray(graph.size) { -1 }
    val queue = ArrayDeque<Int>()
    dist[start] = 0
    queue.addLast(start)
    while (queue.isNotEmpty()) {
        val u = queue.removeFirst()
        for (v in graph[u]) if (dist[v] == -1) {
            dist[v] = dist[u] + 1
            queue.addLast(v)
        }
    }
    return dist
}
```
# DFS
Explore as deep as possible before backtracking — recursion or an explicit stack. Used for cycle detection, connected components, topological sort.
""".trimIndent()),
        LessonEntity(116, 14, "Greedy strategy", 1, """
# When greedy works
The greedy-choice property must hold: a locally optimal choice is part of some globally optimal solution.
# Classic: interval scheduling
Pick the meeting that ends earliest, discard overlaps, repeat — provably optimal.
```
fun maxMeetings(intervals: List<Pair<Int, Int>>): Int {
    var count = 0
    var end = Int.MIN_VALUE
    for ((s, e) in intervals.sortedBy { it.second }) {
        if (s >= end) { count++; end = e }
    }
    return count
}
```
> Always ask: can I construct a counterexample? If yes, greedy is wrong and you likely need DP.
""".trimIndent()),
        LessonEntity(117, 15, "Backtracking patterns", 1, """
# The template
Choose, explore, un-choose.
```
fun permutations(nums: IntArray): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    val path = mutableListOf<Int>()
    val used = BooleanArray(nums.size)
    fun backtrack() {
        if (path.size == nums.size) { result.add(path.toList()); return }
        for (i in nums.indices) {
            if (used[i]) continue
            used[i] = true; path.add(nums[i])
            backtrack()
            used[i] = false; path.removeAt(path.size - 1)
        }
    }
    backtrack()
    return result
}
```
# Pruning
Cut branches that cannot lead to a solution (bounds, sorted input, constraint checks) — the difference between exponential-and-hopeless and exponential-but-fine.
""".trimIndent()),
        LessonEntity(118, 16, "Dynamic programming basics", 1, """
# Recognizing DP
- Overlapping subproblems: the same smaller inputs recur.
- Optimal substructure: the best answer builds on best sub-answers.
# Fibonacci three ways
```
// 1. Naive recursion — O(2^n)
fun fibSlow(n: Int): Long = if (n < 2) n.toLong() else fibSlow(n - 1) + fibSlow(n - 2)

// 2. Memoized (top-down) — O(n)
fun fibMemo(n: Int, memo: LongArray = LongArray(n + 1) { -1 }): Long {
    if (n < 2) return n.toLong()
    if (memo[n] != -1L) return memo[n]
    memo[n] = fibMemo(n - 1, memo) + fibMemo(n - 2, memo)
    return memo[n]
}

// 3. Tabulated (bottom-up), O(1) space
fun fib(n: Int): Long {
    var a = 0L; var b = 1L
    repeat(n) { val next = a + b; a = b; b = next }
    return a
}
```
> Recipe: define the state, write the recurrence, set base cases, choose an evaluation order.
""".trimIndent()),
        LessonEntity(119, 17, "Tries (prefix trees)", 1, """
# Structure
Each node has up to 26 children (for lowercase words) and a flag marking the end of a word.
```
class TrieNode {
    val children = arrayOfNulls<TrieNode>(26)
    var isWord = false
}

class Trie {
    private val root = TrieNode()
    fun insert(word: String) {
        var node = root
        for (c in word) {
            val i = c - 'a'
            if (node.children[i] == null) node.children[i] = TrieNode()
            node = node.children[i]!!
        }
        node.isWord = true
    }
    fun startsWith(prefix: String): Boolean {
        var node = root
        for (c in prefix) node = node.children[c - 'a'] ?: return false
        return true
    }
}
```
| Operation | Time |
| insert / search / prefix | O(word length) |
# Where tries shine
Autocomplete, spell checking, longest common prefix, word games on boards.
""".trimIndent()),
        LessonEntity(120, 18, "Bit manipulation tricks", 1, """
# The operators
AND (and), OR (or), XOR (xor), NOT (inv), shifts (shl, shr, ushr).
# Essential tricks
- Check bit i: (x shr i) and 1
- Set bit i: x or (1 shl i)
- Clear lowest set bit: x and (x - 1)
- Isolate lowest set bit: x and (-x)
- Even/odd: x and 1
```
fun countSetBits(x: Int): Int {
    var n = x
    var count = 0
    while (n != 0) {
        n = n and (n - 1)   // drops the lowest set bit
        count++
    }
    return count
}
```
# XOR superpowers
- a xor a == 0 and a xor 0 == a
- Find the single number that appears once when every other appears twice: XOR everything.
""".trimIndent()),
    )

    val quizzes = listOf(
        QuizEntity(201, 1, "Arrays quiz", 1),
        QuizEntity(202, 3, "Linked lists quiz", 2),
        QuizEntity(203, 6, "Hash tables quiz", 1),
        QuizEntity(204, 8, "Binary search quiz", 2),
        QuizEntity(205, 9, "Sorting quiz", 2),
        QuizEntity(206, 16, "Dynamic programming quiz", 3),
    )

    val questions = listOf(
        QuizQuestionEntity(2011, 201, "mcq", "What is the time complexity of accessing arr[i] in an array?",
            null, "O(1)|O(log n)|O(n)|O(n log n)", 0,
            "The element address is computed directly from the index, so access is constant time."),
        QuizQuestionEntity(2012, 201, "mcq", "Inserting an element at the beginning of an array-backed list of size n costs:",
            null, "O(1)|O(log n)|O(n)|O(n^2)", 2,
            "Every existing element must shift one position to the right — O(n)."),
        QuizQuestionEntity(2013, 201, "tf", "Appending to the end of an ArrayList/mutableListOf is O(1) amortized.",
            null, "True|False", 0,
            "Occasional resizes cost O(n), but averaged over many appends the cost per append is O(1)."),
        QuizQuestionEntity(2014, 201, "code", "What does this print?",
            "val a = intArrayOf(1, 2, 3)\nval b = a\nb[0] = 9\nprintln(a[0])", "1|9|3|It does not compile", 1,
            "Arrays are reference types: a and b point to the same array, so the change is visible through both."),
        QuizQuestionEntity(2021, 202, "mcq", "Deleting the node AFTER a given node in a singly linked list costs:",
            null, "O(1)|O(log n)|O(n)|O(n^2)", 0,
            "Rewire one next pointer: node.next = node.next?.next."),
        QuizQuestionEntity(2022, 202, "tf", "Accessing the k-th element of a linked list is O(1).",
            null, "True|False", 1,
            "You must walk from the head node by node — O(k)."),
        QuizQuestionEntity(2023, 202, "mcq", "Floyd's cycle detection uses:",
            null, "A hash set of nodes|Fast and slow pointers|Sorting|A stack", 1,
            "A pointer moving two steps and one moving one step must meet if a cycle exists — with O(1) extra space."),
        QuizQuestionEntity(2024, 202, "code", "After running this on list 1→2→3, what is the new head's value?",
            "var prev: ListNode? = null\nvar curr = head\nwhile (curr != null) {\n    val next = curr.next\n    curr.next = prev\n    prev = curr\n    curr = next\n}", "1|2|3|null", 2,
            "This is list reversal: 3→2→1, so the new head is 3."),
        QuizQuestionEntity(2031, 203, "mcq", "Average time for get/put in a hash map is:",
            null, "O(1)|O(log n)|O(n)|O(n log n)", 0,
            "With a good hash function, keys spread evenly across buckets."),
        QuizQuestionEntity(2032, 203, "mcq", "The worst case for hash map lookup is O(n). When?",
            null, "The map is full|All keys collide into one bucket|Keys are integers|The map is empty", 1,
            "If every key hashes to the same bucket, lookup degrades to scanning a list of n entries."),
        QuizQuestionEntity(2033, 203, "tf", "Two Sum can be solved in one pass using a hash map.",
            null, "True|False", 0,
            "Check whether target - current is already in the map, then store the current number."),
        QuizQuestionEntity(2041, 204, "mcq", "Binary search requires the input to be:",
            null, "Distinct|Sorted|Positive|Of even length", 1,
            "Discarding half the range is only valid if order tells you which half to discard."),
        QuizQuestionEntity(2042, 204, "mcq", "Why write mid = lo + (hi - lo) / 2 instead of (lo + hi) / 2?",
            null, "It is faster|It avoids integer overflow|It rounds up|No reason", 1,
            "lo + hi can exceed Int.MAX_VALUE for large arrays; the subtraction form cannot."),
        QuizQuestionEntity(2043, 204, "tf", "Binary search on 1,000,000 sorted elements needs at most about 20 comparisons.",
            null, "True|False", 0,
            "log2(1,000,000) ≈ 19.9 — each comparison halves the search space."),
        QuizQuestionEntity(2051, 205, "mcq", "Which sort is stable and guaranteed O(n log n)?",
            null, "Quick sort|Heap sort|Merge sort|Selection sort", 2,
            "Merge sort always splits evenly and its merge preserves the relative order of equal elements."),
        QuizQuestionEntity(2052, 205, "mcq", "Quick sort's worst case O(n^2) happens when:",
            null, "The array is random|Pivots split very unevenly|The array is small|Never", 1,
            "For example, always picking the smallest element as pivot on sorted input."),
        QuizQuestionEntity(2053, 205, "tf", "A comparison-based sort can beat O(n log n) in the worst case.",
            null, "True|False", 1,
            "The decision-tree lower bound proves comparison sorts need Ω(n log n). Counting sort beats it only by not comparing."),
        QuizQuestionEntity(2061, 206, "mcq", "The two properties that make DP applicable are:",
            null, "Sorting and searching|Overlapping subproblems and optimal substructure|Recursion and iteration|Greedy choice and pruning", 1,
            "Subproblems must recur (so caching helps) and combine into an optimal whole."),
        QuizQuestionEntity(2062, 206, "code", "What is the time complexity of this function?",
            "fun fib(n: Int): Long =\n    if (n < 2) n.toLong()\n    else fib(n - 1) + fib(n - 2)", "O(n)|O(n^2)|O(2^n)|O(log n)", 2,
            "Each call spawns two more; the call tree grows exponentially. Memoization reduces it to O(n)."),
        QuizQuestionEntity(2063, 206, "tf", "Top-down memoization and bottom-up tabulation have the same asymptotic time for the same DP.",
            null, "True|False", 0,
            "Both evaluate each state once; they differ in evaluation order and constant factors."),
    )

    val problems = listOf(
        CodingProblemEntity(301, 1, "Two Sum",
            "Given an array of integers nums and an integer target, return the indices of the two numbers that add up to target. Exactly one solution exists; you may not use the same element twice.",
            "Input: nums = [2, 7, 11, 15], target = 9\nOutput: [0, 1]\nBecause nums[0] + nums[1] == 9",
            "2 <= nums.size <= 10^4; only one valid answer exists.",
            "Brute force checks every pair in O(n^2) — can you do one pass?|Store numbers you have already seen in a hash map keyed by value.|For each number, look up target minus that number.",
            """
fun twoSum(nums: IntArray, target: Int): IntArray {
    val seen = HashMap<Int, Int>()   // value -> index
    for (i in nums.indices) {
        val need = target - nums[i]
        seen[need]?.let { return intArrayOf(it, i) }
        seen[nums[i]] = i
    }
    error("No solution")
}
""".trimIndent(),
            "One pass: before storing each number, check whether its complement was already seen. The hash map gives O(1) average lookups.",
            "O(n)", "O(n)", 1,
            pattern = "Hashing",
            patternClues = "'Find a PAIR that sums to a target' — pair problems are about remembering what you have already seen|The answer needs original INDICES, so sorting would destroy information|Constraint n up to 10^4 — an O(n^2) double loop is borderline; you want one pass",
            approach = "Observe: for each number x, its required partner is fully determined: target - x|Candidate 1 — brute force every pair: O(n^2). Works, but you can do better|Candidate 2 — sort + two pointers: O(n log n), but sorting loses the indices you must return|Candidate 3 — hash map of seen value -> index: O(1) lookup of the partner while scanning once|Decision: need fast lookups AND original indices -> hash map. O(n) time, O(n) space"),
        CodingProblemEntity(302, 1, "Maximum Subarray",
            "Find the contiguous subarray with the largest sum and return that sum.",
            "Input: [-2, 1, -3, 4, -1, 2, 1, -5, 4]\nOutput: 6  (subarray [4, -1, 2, 1])",
            "1 <= nums.size <= 10^5",
            "What is the best subarray ending exactly at index i?|If the running sum before i is negative, it can only hurt you.|This is Kadane's algorithm.",
            """
fun maxSubArray(nums: IntArray): Int {
    var best = nums[0]
    var current = nums[0]
    for (i in 1 until nums.size) {
        current = maxOf(nums[i], current + nums[i])
        best = maxOf(best, current)
    }
    return best
}
""".trimIndent(),
            "Kadane's algorithm: at each index keep the best sum ending there — either extend the previous run or start fresh. A one-dimensional DP in disguise.",
            "O(n)", "O(1)", 2,
            pattern = "Dynamic Programming",
            patternClues = "'CONTIGUOUS subarray' + 'maximum sum' — optimizing over all contiguous runs|Trying every subarray is O(n^2) or worse; n up to 10^5 forbids that|Ask the DP question: what is the best answer ENDING exactly at index i?",
            approach = "Observe: the best subarray ending at i either extends the one ending at i-1, or starts fresh at i|That is a recurrence: best(i) = max(nums[i], best(i-1) + nums[i]) — a 1-D DP|Key insight: a negative running sum can never help what follows — drop it. That insight IS Kadane's algorithm|Decision: one pass, two variables (current run, global best). O(n) time, O(1) space"),
        CodingProblemEntity(303, 3, "Reverse Linked List",
            "Reverse a singly linked list and return the new head.",
            "Input: 1 → 2 → 3 → 4 → 5\nOutput: 5 → 4 → 3 → 2 → 1",
            "0 <= number of nodes <= 5000",
            "You need to flip each next pointer.|Keep three references: previous, current, and the saved next.|Save curr.next before overwriting it.",
            """
fun reverseList(head: ListNode?): ListNode? {
    var prev: ListNode? = null
    var curr = head
    while (curr != null) {
        val next = curr.next
        curr.next = prev
        prev = curr
        curr = next
    }
    return prev
}
""".trimIndent(),
            "Walk the list once, redirecting each node's next pointer to the node behind it. prev ends up as the new head.",
            "O(n)", "O(1)", 1,
            pattern = "In-place Reversal",
            patternClues = "'Reverse a LINKED LIST' — no random access, you can only walk node to node|Copying values to an array works but wastes O(n) space — the real exercise is in-place|Whenever you must re-route next pointers, think: prev / curr / saved-next trio",
            approach = "Observe: reversing means every node's next must point backwards instead of forwards|Candidate 1 — copy values out, rebuild reversed: O(n) extra space, misses the point|Candidate 2 — recursion: elegant but O(n) call stack, risky at 5000 nodes|Candidate 3 — iterate with three pointers, flipping one link per step: O(1) space|Decision: iterative in-place reversal. Save curr.next FIRST or you lose the rest of the list"),
        CodingProblemEntity(304, 4, "Valid Parentheses",
            "Given a string containing only ()[]{} decide whether the brackets are balanced and correctly nested.",
            "Input: \"([{}])\" → true\nInput: \"(]\" → false",
            "1 <= s.length <= 10^4",
            "The most recently opened bracket must close first — which structure is that?|Push opening brackets; on a closing bracket, the top of the stack must match.|The string is valid only if the stack ends empty.",
            """
fun isValid(s: String): Boolean {
    val stack = ArrayDeque<Char>()
    val pairs = mapOf(')' to '(', ']' to '[', '}' to '{')
    for (c in s) {
        if (c in pairs.values) stack.addLast(c)
        else if (stack.isEmpty() || stack.removeLast() != pairs[c]) return false
    }
    return stack.isEmpty()
}
""".trimIndent(),
            "A stack mirrors the nesting: every closing bracket must match the most recent unmatched opener.",
            "O(n)", "O(n)", 1,
            pattern = "Stack",
            patternClues = "'Most recently opened must close FIRST' — Last-In-First-Out is literally the stack property|Nesting, matching and balancing problems (brackets, tags, undo) are stack territory|At any moment you only care about the most recent unmatched opener",
            approach = "Observe: when you meet a closing bracket, only the LAST unclosed opener matters|A stack gives exactly that: push openers, pop-and-compare on closers|A mismatch on pop, or popping an empty stack, means invalid — stop immediately|End of string: valid only if the stack is empty (no dangling openers)|Decision: single pass with a stack. O(n) time, O(n) space worst case"),
        CodingProblemEntity(305, 6, "Contains Duplicate",
            "Return true if any value appears at least twice in the array.",
            "Input: [1, 2, 3, 1] → true\nInput: [1, 2, 3, 4] → false",
            "1 <= nums.size <= 10^5",
            "Sorting works in O(n log n) — can a set do better?|HashSet.add returns false when the element already exists.",
            """
fun containsDuplicate(nums: IntArray): Boolean {
    val seen = HashSet<Int>()
    for (n in nums) if (!seen.add(n)) return true
    return false
}
""".trimIndent(),
            "A hash set gives O(1) average membership checks, so a single scan suffices.",
            "O(n)", "O(n)", 1,
            pattern = "Hashing",
            patternClues = "'Does ANY value appear twice?' — a pure membership question; order is irrelevant|You do not need positions or counts, only 'have I seen this before?'|O(1) membership checks are the signature use case of a hash set",
            approach = "Candidate 1 — compare all pairs: O(n^2), too slow for large inputs|Candidate 2 — sort first so duplicates become neighbors: O(n log n), and it mutates the input|Candidate 3 — hash set of seen values, return true on the first repeat: O(n) expected|Decision: hash set — the simplest AND the fastest. You trade O(n) memory for speed"),
        CodingProblemEntity(306, 8, "First Bad Version",
            "Versions 1..n exist and all versions after the first bad one are also bad. Using an isBadVersion(v) check, find the first bad version with as few calls as possible.",
            "Input: n = 5, first bad = 4\nOutput: 4",
            "1 <= first bad <= n <= Int.MAX_VALUE",
            "The versions form a sorted pattern: good...good bad...bad.|Binary search for the boundary.|When mid is bad, the answer is mid or earlier.",
            """
fun firstBadVersion(n: Int, isBad: (Int) -> Boolean): Int {
    var lo = 1
    var hi = n
    while (lo < hi) {
        val mid = lo + (hi - lo) / 2
        if (isBad(mid)) hi = mid else lo = mid + 1
    }
    return lo
}
""".trimIndent(),
            "Binary search on the boundary between good and bad. Note lo + (hi - lo) / 2 to avoid overflow with n near Int.MAX_VALUE.",
            "O(log n)", "O(1)", 1,
            pattern = "Binary Search",
            patternClues = "Versions look like GOOD...GOOD BAD...BAD — a sorted, monotonic boolean sequence|You are hunting a BOUNDARY (the first bad one), not a specific value|Each isBadVersion call is expensive — 'minimize calls' screams logarithmic",
            approach = "Observe: once a version is bad, all later ones are bad — the answer is a boundary in monotonic data|Monotonic + find-the-boundary = binary search on the answer|If mid is bad, the first bad is at mid or earlier -> hi = mid; otherwise lo = mid + 1|Compute mid as lo + (hi - lo) / 2 to dodge integer overflow|Decision: boundary binary search. O(log n) calls instead of O(n)"),
        CodingProblemEntity(307, 10, "Maximum Depth of Binary Tree",
            "Return the number of nodes along the longest path from the root down to the farthest leaf.",
            "Input: root = [3, 9, 20, null, null, 15, 7]\nOutput: 3",
            "0 <= number of nodes <= 10^4",
            "The depth of a tree relates to the depths of its subtrees.|depth(node) = 1 + max(depth(left), depth(right)).",
            """
fun maxDepth(root: TreeNode?): Int =
    if (root == null) 0
    else 1 + maxOf(maxDepth(root.left), maxDepth(root.right))
""".trimIndent(),
            "A textbook structural recursion: the empty tree is the base case, and each node adds one to the deeper subtree.",
            "O(n)", "O(h) call stack", 1,
            pattern = "DFS / Recursion",
            patternClues = "A TREE question about depth — trees are recursively defined, so think recursively|The answer at a node depends only on the answers for its two subtrees|No ordering or path constraints — any traversal that touches every node works",
            approach = "Observe: depth(node) = 1 + max(depth(left), depth(right)), and depth(null) = 0|That recurrence maps one-to-one onto recursive DFS — three lines of code|Alternative: BFS counting levels also works, but needs an explicit queue|Decision: recursive DFS for clarity. O(n) time, O(h) stack where h is the tree height"),
        CodingProblemEntity(308, 12, "Kth Largest Element",
            "Find the k-th largest element in an unsorted array (not the k-th distinct).",
            "Input: nums = [3, 2, 1, 5, 6, 4], k = 2\nOutput: 5",
            "1 <= k <= nums.size <= 10^5",
            "Sorting costs O(n log n) — a heap of size k does better.|Keep a min-heap of the k largest values seen so far.|If the heap exceeds size k, remove its minimum.",
            """
import java.util.PriorityQueue

fun findKthLargest(nums: IntArray, k: Int): Int {
    val heap = PriorityQueue<Int>()   // min-heap
    for (n in nums) {
        heap.add(n)
        if (heap.size > k) heap.poll()
    }
    return heap.peek()
}
""".trimIndent(),
            "The min-heap holds the k largest elements; its root is exactly the k-th largest. Each element costs O(log k).",
            "O(n log k)", "O(k)", 2,
            pattern = "Heap / Top-K",
            patternClues = "'K-th LARGEST' or 'top K' — you need PARTIAL order, not a fully sorted array|Fully sorting computes far more than was asked — a smell that a heap can do less work|Maintaining 'the best k so far' while streaming through data is the min-heap idiom",
            approach = "Candidate 1 — sort everything and index from the end: O(n log n), simple but overkill|Candidate 2 — min-heap capped at size k: push each value, pop when size exceeds k; the root is the answer: O(n log k)|Candidate 3 — quickselect: O(n) average but O(n^2) worst case, and fiddlier to write|Decision: size-k min-heap — near-optimal, easy to write, and works on streams too"),
        CodingProblemEntity(309, 16, "Climbing Stairs",
            "You can climb 1 or 2 steps at a time. How many distinct ways are there to reach step n?",
            "Input: n = 3\nOutput: 3  (1+1+1, 1+2, 2+1)",
            "1 <= n <= 45",
            "How do you arrive at step n? From n-1 or n-2.|ways(n) = ways(n-1) + ways(n-2) — recognize the sequence?|Two rolling variables give O(1) space.",
            """
fun climbStairs(n: Int): Int {
    var a = 1
    var b = 1
    repeat(n - 1) {
        val next = a + b
        a = b
        b = next
    }
    return b
}
""".trimIndent(),
            "The recurrence is Fibonacci. Bottom-up tabulation with two variables avoids both recursion and an array.",
            "O(n)", "O(1)", 1,
            pattern = "Dynamic Programming",
            patternClues = "'HOW MANY ways to reach...' — counting paths is a classic DP giveaway|The final move was 1 or 2 steps — the problem splits into identical smaller subproblems|Naive recursion recomputes the same values — overlapping subproblems = DP",
            approach = "Observe: ways(n) = ways(n-1) + ways(n-2) — you arrived from one step below or from two|That is the Fibonacci recurrence; plain recursion explodes exponentially from recomputation|Fix it by memoizing top-down, or building bottom-up from ways(1)=1, ways(2)=2|Only the last two values are ever needed -> keep two rolling variables|Decision: bottom-up DP with two variables. O(n) time, O(1) space"),
        CodingProblemEntity(310, 18, "Single Number",
            "Every element appears twice except one. Find that single one in linear time and constant space.",
            "Input: [4, 1, 2, 1, 2]\nOutput: 4",
            "1 <= nums.size <= 3 * 10^4",
            "A hash set works but uses O(n) space — the constraint hints at bit tricks.|a xor a == 0 and a xor 0 == a.|XOR is commutative: pairs cancel out.",
            """
fun singleNumber(nums: IntArray): Int {
    var result = 0
    for (n in nums) result = result xor n
    return result
}
""".trimIndent(),
            "XOR-ing everything cancels the pairs and leaves the unpaired value — the signature bit-manipulation trick.",
            "O(n)", "O(1)", 1,
            pattern = "Bit Manipulation",
            patternClues = "'Every element appears TWICE except one' + 'O(1) extra space' — the space limit rules out a hash set|Pairs that must cancel each other out hint at XOR: x xor x = 0|Order does not matter — XOR is commutative and associative, so scrambling is fine",
            approach = "Candidate 1 — hash set, add on first sight and remove on second: O(n) time but O(n) space — violates the constraint|Candidate 2 — sort and scan neighbors: O(n log n), mutates the input|Candidate 3 — XOR every number together: pairs annihilate to 0, the loner survives|Decision: a single XOR fold. O(n) time, O(1) space — the constraint itself pointed at the trick"),
    )


    val patterns = listOf(
        PatternEntity(401, "Two Pointers",
            "Two indices moving toward each other (or in tandem) replace checking every pair.",
            "Instead of testing all O(n^2) pairs, keep one pointer at each end (or both at the front) and move the one that cannot possibly be part of a better answer. Sorted order is what makes each move safe: it tells you which side to advance.",
            "The array is SORTED — or you may sort it without losing information you need|A PAIR / triplet / range must hit a target or satisfy a condition|'Remove duplicates in place', 'move zeroes', 'partition' — rearranging within one array|Compare from both ends: palindrome checks, container-with-most-water shapes|Something that looks O(n^2) is requested with O(1) extra space",
            "You must return ORIGINAL indices and sorting would scramble them -> Hashing|The data cannot be sorted and has no order to exploit -> Hashing|A window must grow AND shrink based on what it contains -> Sliding Window",
            """
// Pair-with-target in a SORTED array
var lo = 0
var hi = nums.lastIndex
while (lo < hi) {
    val sum = nums[lo] + nums[hi]
    when {
        sum == target -> return true
        sum < target  -> lo++   // left value too small — only moving lo can help
        else          -> hi--   // sum too big — only moving hi can help
    }
}
""".trimIndent()),
        PatternEntity(402, "Sliding Window",
            "Grow a window on the right, shrink it on the left — adjacent windows share almost all their work.",
            "For CONTIGUOUS subarrays or substrings, brute force re-examines each window from scratch. A sliding window updates counts incrementally: extend the right edge one element at a time, and while the window breaks the rule, advance the left edge. Each element enters and leaves once — O(n).",
            "'Longest / shortest CONTIGUOUS subarray or substring such that ...'|A rule about the window contents: at most K distinct, no repeats, sum below a limit|'At most K' / 'exactly K' phrasing|All numbers are non-negative, so growing the window moves the sum one way only",
            "Values can be NEGATIVE and you need an exact target sum -> Prefix Sum + Hashing|The elements need not be contiguous (subsequence) -> DP or Greedy|You only ever compare the two ends -> Two Pointers",
            """
var left = 0
var best = 0
for (right in s.indices) {
    add(s[right])                    // window grows
    while (windowInvalid()) {
        remove(s[left]); left++      // window shrinks until valid
    }
    best = maxOf(best, right - left + 1)
}
""".trimIndent()),
        PatternEntity(403, "Prefix Sum",
            "Precompute running totals so any range sum becomes one subtraction.",
            "prefix[i] = sum of the first i elements. Then sum(l..r) = prefix[r+1] - prefix[l] in O(1). Pair it with a hash map of previously seen prefix values to count subarrays hitting an exact target — even with negative numbers, where sliding windows break down.",
            "MANY range-sum queries over a static array|'Count subarrays whose sum equals K' — especially with NEGATIVE numbers allowed|The answer is a difference between two running states|You keep re-adding the same long stretches of numbers",
            "Only one pass and one query -> just accumulate directly|All values non-negative and you need longest/shortest window -> Sliding Window",
            """
val prefix = LongArray(nums.size + 1)
for (i in nums.indices) prefix[i + 1] = prefix[i] + nums[i]
// sum of nums[l..r]:
val rangeSum = prefix[r + 1] - prefix[l]

// Count subarrays summing to k (negatives OK):
val seen = HashMap<Long, Int>().apply { put(0L, 1) }
var run = 0L; var count = 0
for (x in nums) {
    run += x
    count += seen.getOrDefault(run - k, 0)
    seen[run] = (seen[run] ?: 0) + 1
}
""".trimIndent()),
        PatternEntity(404, "Hashing",
            "Trade O(n) memory for O(1) answers to 'have I seen this — and where?'",
            "A hash set answers membership; a hash map attaches data (index, count, list) to a computed key. Most 'find a pair', 'find duplicates', or 'group by property' problems collapse to a single pass once lookups are constant time.",
            "'Have I seen this value before?' — pure membership|You need value -> index or value -> count in O(1)|GROUP items by a derived key (anagrams -> sorted letters)|Pair-with-target on UNSORTED data where original indices matter|Frequency questions: most common, first unique",
            "O(1) extra space is demanded -> Bit Manipulation or Two Pointers on sorted data|Range or order queries (nearest, k-th smallest) — hashes destroy order -> Binary Search / Heap",
            """
// Membership
val seen = HashSet<Int>()
for (x in nums) if (!seen.add(x)) return true   // duplicate found

// Value -> index
val at = HashMap<Int, Int>()
for ((i, x) in nums.withIndex()) {
    at[target - x]?.let { return intArrayOf(it, i) }
    at[x] = i
}
""".trimIndent()),
        PatternEntity(405, "Stack",
            "When only the most recent unresolved thing matters, push it and deal with it later.",
            "A stack holds pending items so the newest is always on top. Matching brackets, undo operations, evaluating nested expressions — all are 'last opened, first closed'. The monotonic-stack variant keeps the stack sorted to answer 'next greater / smaller element' in one pass.",
            "'Most recently opened must close first' — brackets, tags, nested scopes|'NEXT GREATER / next smaller element' -> monotonic stack|Undo / backtrack exactly one step|Collapse adjacent items that interact (asteroid collisions, string dedup)",
            "You need the OLDEST item first -> Queue / BFS|You need the min or max of everything, not the most recent -> Heap",
            """
// Balanced brackets
val stack = ArrayDeque<Char>()
for (c in s) when (c) {
    '(', '[', '{' -> stack.addLast(c)
    else -> if (stack.isEmpty() || !matches(stack.removeLast(), c)) return false
}
return stack.isEmpty()
""".trimIndent()),
        PatternEntity(406, "Fast & Slow Pointers",
            "Two runners at different speeds reveal cycles and midpoints without extra memory.",
            "Move one pointer a step at a time and another two steps. In a cycle the fast one must lap the slow one; in a straight list the fast one reaching the end leaves the slow one at the middle. All with O(1) space.",
            "Detect a CYCLE in a linked list or an iterated function (happy number)|Find the MIDDLE of a linked list in one pass|K-th node from the end without knowing the length|'O(1) space' on a linked-list traversal question",
            "Random access is available (arrays) — indices are simpler|You may use O(n) space — a hash set of visited nodes is easier to reason about",
            """
var slow = head
var fast = head
while (fast?.next != null) {
    slow = slow?.next          // 1 step
    fast = fast.next?.next     // 2 steps
    if (slow === fast) return true   // cycle
}
// no cycle; slow now sits at the middle
""".trimIndent()),
        PatternEntity(407, "In-place Reversal",
            "Re-route next pointers with a prev / curr / saved-next trio — no extra list.",
            "Linked-list rearrangements come down to disciplined pointer surgery. Save the next node BEFORE overwriting the link, flip the link backwards, then advance. The same three-pointer dance reverses whole lists, sublists, and k-groups.",
            "'Reverse a linked list' (or a sublist between positions) with O(1) space|Reorder or interleave a list from both ends|Any task where next pointers must be REWIRED rather than values copied",
            "Copying values to an array is allowed and simpler — and n is small|You only need to READ in reverse -> recursion or a stack",
            """
var prev: ListNode? = null
var curr = head
while (curr != null) {
    val next = curr.next   // save FIRST — or the rest of the list is lost
    curr.next = prev       // flip the link
    prev = curr            // advance prev
    curr = next            // advance curr
}
// prev is the new head
""".trimIndent()),
        PatternEntity(408, "Binary Search",
            "Monotonic data lets every comparison discard half the search space.",
            "Binary search needs one thing: a predicate that flips once from false to true across the range. That covers sorted arrays, but also 'first version that is bad', and 'smallest capacity that ships in D days' — searching the ANSWER space rather than the array.",
            "The input is SORTED, or some property is MONOTONIC over it|Find a BOUNDARY: first/last occurrence, first true, insertion point|'Minimize the maximum' or 'maximize the minimum' -> binary search on the answer|O(log n) is requested, or each check is expensive ('minimize API calls')",
            "No monotonic structure anywhere — half-discarding is unsafe|The array is tiny and unsorted — a linear scan is simpler and just as fast",
            """
// Boundary form: first index where check(i) is true
var lo = 0
var hi = n - 1
while (lo < hi) {
    val mid = lo + (hi - lo) / 2   // avoids overflow
    if (check(mid)) hi = mid       // answer is mid or earlier
    else lo = mid + 1              // answer is after mid
}
// lo is the first true
""".trimIndent()),
        PatternEntity(409, "BFS",
            "Explore level by level with a queue — the first time you reach a node is the shortest way there.",
            "Breadth-first search processes everything at distance d before anything at d+1. On UNWEIGHTED graphs and grids that guarantees minimal step counts. Seed the queue with several starts for multi-source spread problems.",
            "SHORTEST path / FEWEST steps on an unweighted graph, grid, or state space|Level-order processing (tree by levels, zigzag)|Simultaneous spread from many sources (rotting oranges, fire)|Minimum number of transformations from A to B",
            "Edges have different WEIGHTS -> Dijkstra|You must enumerate all paths or configurations -> DFS / Backtracking|Deep recursion is natural and shortest-ness is irrelevant -> DFS",
            """
val queue = ArrayDeque<Node>()
val visited = HashSet<Node>()
queue.add(start); visited.add(start)
var steps = 0
while (queue.isNotEmpty()) {
    repeat(queue.size) {               // one whole level
        val node = queue.removeFirst()
        if (node == goal) return steps
        for (next in neighbors(node))
            if (visited.add(next)) queue.add(next)
    }
    steps++
}
""".trimIndent()),
        PatternEntity(410, "DFS / Recursion",
            "Let the call stack walk the structure: solve each subtree, then combine.",
            "Trees and graphs are recursively defined, so recursive descent mirrors the data. Answer at a node = combine(answers of children). Backtracking is DFS over CHOICES: place a candidate, recurse, then undo — pruning dead branches early.",
            "Tree questions where a node's answer combines its subtrees' answers (depth, sums, validity)|Visit every cell/component: islands, flood fill, connected components|Generate ALL subsets / permutations / combinations|Constraint puzzles: choose, recurse, UNDO (N-Queens, Sudoku)",
            "SHORTEST path in an unweighted space -> BFS|Only a running best over subproblems is needed, no exploration -> DP|Recursion depth could blow the stack (5000+ deep lists) -> iterate",
            """
// Structural recursion on a tree
fun depth(node: TreeNode?): Int {
    if (node == null) return 0
    return 1 + maxOf(depth(node.left), depth(node.right))
}

// Backtracking over choices
fun backtrack(path: MutableList<Int>) {
    if (isComplete(path)) { record(path); return }
    for (choice in options(path)) {
        path.add(choice)     // choose
        backtrack(path)      // explore
        path.removeLast()    // un-choose
    }
}
""".trimIndent()),
        PatternEntity(411, "Heap / Top-K",
            "Keep only the k best in a small heap — partial order beats a full sort.",
            "A heap pops its smallest (or largest) element in O(log size). To track the k largest items, keep a MIN-heap capped at size k: anything smaller than its root can never matter. You get O(n log k) instead of O(n log n), and it works on streams.",
            "'K largest / K smallest / K most frequent'|Repeatedly need the current min or max while items keep arriving|Merge K sorted lists or streams|Data is too large or endless to sort in full",
            "K equals 1 -> a single max/min variable|You need ALL items in order -> just sort|Exact k-th once, average case fine -> quickselect",
            """
// K largest: min-heap capped at size k
val heap = java.util.PriorityQueue<Int>()   // min-heap
for (x in nums) {
    heap.add(x)
    if (heap.size > k) heap.poll()   // evict the smallest
}
val kthLargest = heap.peek()
""".trimIndent()),
        PatternEntity(412, "Greedy",
            "Take the locally best option — valid only when you can argue it never blocks the global best.",
            "Greedy sorts by the right key and sweeps, committing to the best available choice at each step. It is fast and simple, but correctness needs an exchange argument: swapping any optimal solution toward the greedy choice never makes it worse. If choices interact, greedy silently fails — that is DP territory.",
            "'Minimum number of intervals / jumps / coins-with-canonical-denominations'|Interval scheduling: sort by END time, keep what fits|At each step one choice clearly dominates and consumes nothing needed later|You can sketch the exchange argument in one sentence",
            "A cheap choice now can block a better combination later -> DP|You cannot articulate WHY local best is safe — test small counterexamples first|'Count the ways' -> DP",
            """
// Max non-overlapping intervals: sort by end, sweep
intervals.sortBy { it.end }
var taken = 0
var freeFrom = Int.MIN_VALUE
for (iv in intervals) {
    if (iv.start >= freeFrom) {   // fits — taking it is provably safe
        taken++
        freeFrom = iv.end
    }
}
""".trimIndent()),
        PatternEntity(413, "Dynamic Programming",
            "Define the answer to a subproblem, find the recurrence, reuse instead of recompute.",
            "DP applies when the problem has optimal substructure (big answers build from smaller ones) and overlapping subproblems (plain recursion revisits the same states). Name the state precisely — 'best sum ending at i', 'ways to reach step n' — write the recurrence, then memoize top-down or fill a table bottom-up.",
            "'How MANY ways ...' — counting paths or arrangements|'Minimum / maximum cost' over a SEQUENCE of decisions|Take-or-skip choices where today constrains tomorrow (house robber, knapsack)|Naive recursion recomputes identical calls|The answer at i depends only on answers at smaller states",
            "Subproblems never repeat -> plain recursion / divide and conquer|A one-line exchange argument makes local choices safe -> Greedy|The 'state' needs the entire history — look for a smaller sufficient state first",
            """
// Bottom-up with rolling variables (Climbing Stairs)
if (n <= 2) return n
var twoBack = 1
var oneBack = 2
for (step in 3..n) {
    val here = oneBack + twoBack   // the recurrence
    twoBack = oneBack
    oneBack = here
}
return oneBack
""".trimIndent()),
        PatternEntity(414, "Bit Manipulation",
            "XOR cancellation and bit tricks solve pair puzzles in O(1) space.",
            "Bits give constant-space superpowers: x xor x == 0 makes duplicate pairs annihilate; n and (n - 1) clears the lowest set bit; a 20-element set fits in a single Int as a bitmask. When a constraint forbids the natural hash set, check whether bits can carry the state instead.",
            "'Every element appears twice except ...' + O(1) space — pairs cancel via XOR|Count or test set bits, powers of two, parity|Toggle / include-exclude over a SMALL set (n <= 20) -> bitmask subsets|The natural solution wants a hash set but space is capped",
            "Values or set sizes exceed what bits can encode -> Hashing|Readability matters more than the constant factor and space is free",
            """
// Lone element among pairs
var acc = 0
for (x in nums) acc = acc xor x   // pairs cancel to 0
return acc

// Classic tricks
val isPowerOfTwo = n > 0 && (n and (n - 1)) == 0
val lowestSetBit = n and (-n)
""".trimIndent()),
        PatternEntity(415, "Merge Intervals",
            "Sort by start, then sweep — overlapping intervals collapse into one.",
            "Interval problems become mechanical once sorted by start: walk left to right, and each new interval either overlaps the current merged block (extend its end) or begins a fresh one. The same sweep answers insert-interval, meeting-rooms and free-time questions; counting simultaneous overlaps gives minimum rooms.",
            "The input is a list of INTERVALS / ranges / bookings / meetings|Words like 'merge', 'insert', 'overlap', 'conflict', 'free time'|'Minimum number of meeting rooms' — peak simultaneous overlap|Endpoint ORDER on a timeline decides everything",
            "Only one fixed range is compared — a plain if suffices|Intervals change dynamically with many live queries — needs an interval tree (advanced)",
            """
// intervals as IntArray(start, end)
val sorted = intervals.sortedBy { it[0] }
val merged = mutableListOf(sorted[0].clone())
for (iv in sorted.drop(1)) {
    val last = merged.last()
    if (iv[0] <= last[1]) last[1] = maxOf(last[1], iv[1])  // overlap: extend
    else merged.add(iv.clone())                            // gap: start new block
}
""".trimIndent()),
        PatternEntity(416, "Cyclic Sort",
            "Numbers 1..n belong at index value-1 — swap each into its home in O(n).",
            "When an array holds numbers from a fixed range 1..n, the index itself can act as the hash: value v belongs at position v-1. Swap every element into its home; a second pass exposes whichever positions disagree — those are your missing or duplicated numbers. O(n) time, O(1) space, no sorting library needed.",
            "The array holds numbers in a FIXED RANGE like 1..n or 0..n|'Find the missing / duplicated / all missing numbers'|O(n) time AND O(1) space demanded — ruling out both sorting and hashing|Indices can double as buckets",
            "Values are unbounded or arbitrary -> Hashing|The array must not be modified -> treat the duplicate as a linked-list cycle (Fast & Slow) or binary search on the value range",
            """
var i = 0
while (i < nums.size) {
    val home = nums[i] - 1                     // where nums[i] belongs
    if (nums[i] in 1..nums.size && nums[i] != nums[home]) {
        val t = nums[i]; nums[i] = nums[home]; nums[home] = t
    } else i++
}
// second pass: any index with nums[i] != i + 1 reveals the anomaly
""".trimIndent()),
        PatternEntity(417, "Monotonic Stack",
            "Keep the stack sorted; every pop means 'I just found your next greater element.'",
            "A monotonic stack holds indices whose values are kept in decreasing (or increasing) order. Each new element pops everything it beats — and for each popped index, the newcomer IS its answer. Every element pushes once and pops once, so an apparently O(n^2) 'look ahead for each element' collapses to O(n).",
            "'NEXT GREATER / next smaller / previous greater' element|Daily temperatures: 'how many days until something warmer'|Largest rectangle in a histogram, trapping rain water|Each element is waiting for the first later element that beats it",
            "You need the min/max of EVERYTHING at all times -> Heap|Simple open/close pair matching -> plain Stack is enough",
            """
val next = IntArray(nums.size) { -1 }
val stack = ArrayDeque<Int>()              // indices; values strictly decreasing
for (i in nums.indices) {
    while (stack.isNotEmpty() && nums[stack.last()] < nums[i]) {
        next[stack.removeLast()] = i       // i is their next greater element
    }
    stack.addLast(i)
}
""".trimIndent()),
        PatternEntity(418, "Topological Sort",
            "Order tasks so every prerequisite comes first — repeatedly peel nodes with no incoming edges.",
            "On a directed acyclic graph where an edge means must-come-before, Kahn's algorithm computes a valid order: start with all zero-indegree nodes, remove them, decrement their neighbors, repeat. If you cannot consume every node, the leftovers form a cycle — no valid ordering exists.",
            "'Prerequisites', 'dependencies', 'build order', 'course schedule'|A DIRECTED graph where edges mean must-come-before|'Can they all be finished?' — cycle detection in disguise|An item may be processed only after everything it depends on",
            "Relations are UNDIRECTED -> Union-Find or plain traversal|You want shortest paths, not an ordering -> BFS or Dijkstra",
            """
val indegree = IntArray(n)
for (v in 0 until n) for (next in adj[v]) indegree[next]++
val queue = ArrayDeque<Int>()
for (v in 0 until n) if (indegree[v] == 0) queue.add(v)
val order = mutableListOf<Int>()
while (queue.isNotEmpty()) {
    val v = queue.removeFirst()
    order.add(v)
    for (next in adj[v]) if (--indegree[next] == 0) queue.add(next)
}
// order.size < n  ->  a cycle exists: no valid ordering
""".trimIndent()),
        PatternEntity(419, "Union-Find",
            "Near-O(1) 'are these two connected?' — merge groups as edges arrive.",
            "Union-Find (disjoint set) maintains groups under merging. find(x) walks to a group's representative (with path compression flattening as it goes); union(a, b) links two representatives. It shines when edges arrive one by one and you must answer connectivity between the arrivals — DFS would have to re-run every time.",
            "'Connected components', 'friend circles', 'merge accounts'|Edges arrive INCREMENTALLY with connectivity questions in between|'Find the redundant connection' — the edge that closes a cycle|Grouping by an equivalence relation (same network, same island)",
            "You need the actual PATH, not just yes/no connectivity -> BFS / DFS|Directed prerequisite ordering -> Topological Sort",
            """
val parent = IntArray(n) { it }
fun find(x: Int): Int {
    var r = x
    while (parent[r] != r) { parent[r] = parent[parent[r]]; r = parent[r] }
    return r
}
fun union(a: Int, b: Int): Boolean {
    val ra = find(a); val rb = find(b)
    if (ra == rb) return false     // already connected: this edge closes a cycle
    parent[ra] = rb
    return true
}
""".trimIndent()),
        PatternEntity(420, "Trie",
            "A tree of characters — every word sharing a prefix shares a path.",
            "A trie stores a dictionary letter by letter: the root is the empty string and each edge adds one character, so lookups and prefix queries cost O(word length) regardless of dictionary size. It is THE structure whenever the word 'prefix' appears, and it powers letter-by-letter exploration like word-search with wildcards.",
            "The word 'PREFIX' anywhere: autocomplete, startsWith, longest common prefix|A dictionary of many words queried repeatedly|Word search with wildcards or character-by-character branching|Maximum XOR of numbers -> bitwise trie (advanced variant)",
            "One-off membership checks with no prefix structure -> HashSet|SUBSTRING (not prefix) matching -> string algorithms like KMP",
            """
class TrieNode {
    val child = arrayOfNulls<TrieNode>(26)
    var isWord = false
}
fun insert(root: TrieNode, word: String) {
    var node = root
    for (c in word) {
        val i = c - 'a'
        if (node.child[i] == null) node.child[i] = TrieNode()
        node = node.child[i]!!
    }
    node.isWord = true
}
""".trimIndent()),
        PatternEntity(421, "Two Heaps",
            "A max-heap for the lower half, a min-heap for the upper — the median lives between their tops.",
            "Split a stream into two balanced halves: a max-heap owning the smaller values and a min-heap owning the larger. Keep sizes within one of each other and every median query is just peeking at the tops. The general trick: whenever a problem watches a BOUNDARY inside changing data, park each side in its own heap.",
            "'MEDIAN of a data stream' / running median|Keep two halves of dynamic data balanced around a boundary|Sliding-window median|Pair a 'best below threshold' with a 'best above threshold' as data changes",
            "The whole dataset is known upfront and one median is needed -> sort or quickselect|Only a single global min OR max matters -> one heap",
            """
val lower = java.util.PriorityQueue<Int>(java.util.Collections.reverseOrder()) // max-heap
val upper = java.util.PriorityQueue<Int>()                                     // min-heap
fun add(x: Int) {
    lower.add(x)
    upper.add(lower.poll())                            // keep halves ordered
    if (upper.size > lower.size) lower.add(upper.poll())
}
fun median(): Double =
    if (lower.size > upper.size) lower.peek().toDouble()
    else (lower.peek() + upper.peek()) / 2.0
""".trimIndent()),
    )

    val builtInDeck = DeckEntity(name = "DSA Essentials", description = "Built-in core concepts deck", isBuiltIn = true)

    val cards = listOf(
        FlashcardEntity(deckId = 0, topicSlug = "arrays", question = "Time to access arr[i] in an array?", answer = "O(1)",
            explanation = "The address is computed directly from the index."),
        FlashcardEntity(deckId = 0, topicSlug = "arrays", question = "Cost of inserting into the middle of an array?", answer = "O(n)",
            explanation = "All later elements shift one position."),
        FlashcardEntity(deckId = 0, topicSlug = "strings", question = "Why avoid string concatenation with + in loops?", answer = "Each + copies the whole string → O(n^2) total. Use StringBuilder.",
            explanation = "Strings are immutable in Kotlin/Java."),
        FlashcardEntity(deckId = 0, topicSlug = "linked-lists", question = "Access by index in a linked list?", answer = "O(n)",
            explanation = "You must walk node by node from the head."),
        FlashcardEntity(deckId = 0, topicSlug = "linked-lists", question = "Which technique detects a cycle in O(1) space?", answer = "Floyd's fast & slow pointers",
            explanation = "If a cycle exists, a 2x-speed pointer must lap the 1x pointer."),
        FlashcardEntity(deckId = 0, topicSlug = "stacks", question = "Stack order?", answer = "LIFO — last in, first out",
            explanation = "Push and pop happen at the same end."),
        FlashcardEntity(deckId = 0, topicSlug = "queues", question = "Queue order?", answer = "FIFO — first in, first out",
            explanation = "Enqueue at the back, dequeue at the front."),
        FlashcardEntity(deckId = 0, topicSlug = "queues", question = "Which traversal uses a queue?", answer = "Breadth-first search (level order)",
            explanation = "BFS processes nodes in the order they were discovered."),
        FlashcardEntity(deckId = 0, topicSlug = "hash-tables", question = "Average lookup time in a hash map?", answer = "O(1)",
            explanation = "A good hash function spreads keys evenly across buckets."),
        FlashcardEntity(deckId = 0, topicSlug = "hash-tables", question = "Worst-case lookup in a hash map, and why?", answer = "O(n) — all keys collide into one bucket",
            explanation = "Degenerates to scanning a list."),
        FlashcardEntity(deckId = 0, topicSlug = "recursion", question = "Two required parts of any recursion?", answer = "A base case and a recursive case that shrinks the input",
            explanation = "Without a reachable base case, recursion never terminates."),
        FlashcardEntity(deckId = 0, topicSlug = "searching", question = "Binary search time complexity?", answer = "O(log n)",
            explanation = "Each comparison halves the remaining range.", difficulty = 1),
        FlashcardEntity(deckId = 0, topicSlug = "searching", question = "Precondition for binary search?", answer = "The data must be sorted",
            explanation = "Order is what lets you discard half the range."),
        FlashcardEntity(deckId = 0, topicSlug = "sorting", question = "A stable, guaranteed O(n log n) sort?", answer = "Merge sort",
            explanation = "Always splits evenly; merging preserves order of equal keys."),
        FlashcardEntity(deckId = 0, topicSlug = "sorting", question = "Quick sort worst case and trigger?", answer = "O(n^2), when pivots split very unevenly",
            explanation = "E.g., always picking the min on already-sorted input."),
        FlashcardEntity(deckId = 0, topicSlug = "trees", question = "Which DFS traversal of a BST yields sorted order?", answer = "Inorder (left, node, right)",
            explanation = "Follows directly from the BST invariant."),
        FlashcardEntity(deckId = 0, topicSlug = "bst", question = "BST search time when balanced vs degenerate?", answer = "O(log n) balanced, O(n) degenerate",
            explanation = "A BST built from sorted input becomes a linked list."),
        FlashcardEntity(deckId = 0, topicSlug = "heaps", question = "Heap insert and remove-root complexity?", answer = "O(log n) each",
            explanation = "Sift-up / sift-down travel at most the tree height."),
        FlashcardEntity(deckId = 0, topicSlug = "heaps", question = "Best structure for streaming top-K elements?", answer = "A min-heap of size K",
            explanation = "O(n log k) versus O(n log n) for full sorting.", difficulty = 3),
        FlashcardEntity(deckId = 0, topicSlug = "graphs", question = "Best representation for sparse graphs?", answer = "Adjacency list — O(V + E) space",
            explanation = "A matrix wastes O(V^2) space when edges are few."),
        FlashcardEntity(deckId = 0, topicSlug = "graphs", question = "Shortest path in an UNWEIGHTED graph?", answer = "BFS",
            explanation = "BFS explores in rings of increasing distance from the source."),
        FlashcardEntity(deckId = 0, topicSlug = "greedy", question = "When is a greedy algorithm provably correct?", answer = "When the greedy-choice property and optimal substructure hold",
            explanation = "Otherwise a counterexample usually exists — consider DP.", difficulty = 3),
        FlashcardEntity(deckId = 0, topicSlug = "backtracking", question = "The three-step backtracking template?", answer = "Choose → explore (recurse) → un-choose",
            explanation = "Undoing the choice restores state for the next branch."),
        FlashcardEntity(deckId = 0, topicSlug = "dp", question = "Two properties that justify dynamic programming?", answer = "Overlapping subproblems + optimal substructure",
            explanation = "Caching only helps if subproblems repeat and compose optimally."),
        FlashcardEntity(deckId = 0, topicSlug = "dp", question = "Naive recursive Fibonacci complexity?", answer = "O(2^n) — memoization reduces it to O(n)",
            explanation = "The call tree doubles at each level without caching.", difficulty = 3,
            codeSnippet = "fun fib(n: Int): Long =\n    if (n < 2) n.toLong()\n    else fib(n - 1) + fib(n - 2)"),
        FlashcardEntity(deckId = 0, topicSlug = "tries", question = "Trie insert/search complexity?", answer = "O(L), the length of the word",
            explanation = "Independent of how many words are stored."),
        FlashcardEntity(deckId = 0, topicSlug = "bits", question = "What does x and (x - 1) do?", answer = "Clears the lowest set bit of x",
            explanation = "Basis of Kernighan's set-bit counting trick."),
        FlashcardEntity(deckId = 0, topicSlug = "bits", question = "Why does XOR-ing an array find the element appearing once (others twice)?", answer = "Pairs cancel: a xor a = 0, and a xor 0 = a",
            explanation = "XOR is commutative and associative, so order doesn't matter."),
    )
}

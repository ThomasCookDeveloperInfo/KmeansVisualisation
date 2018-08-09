package genericdatastructures

open class BinaryTreeNode<T>(open var data: T,
                             open var left: BinaryTreeNode<T>? = null,
                             open var right: BinaryTreeNode<T>? = null) where T : Comparable<T>

abstract class BinaryTree<T>(protected open val root: BinaryTreeNode<T>) where T : Comparable<T> {
    abstract fun insert(data: T, current: BinaryTreeNode<T>? = root) : BinaryTreeNode<T>
    abstract fun find(data: T, current: BinaryTreeNode<T>? = root) : BinaryTreeNode<T>?
    abstract fun delete(data: T, current: BinaryTreeNode<T>? = root) : BinaryTreeNode<T>?
}

open class SimpleBinaryTree<T> (override val root: BinaryTreeNode<T>) : BinaryTree<T>(root) where T : Comparable<T> {
    override fun insert(data: T, current: BinaryTreeNode<T>?): BinaryTreeNode<T> {
        if (current === null)
            return BinaryTreeNode(data)

        if (current.data < data) {
            current.right = insert(data, current.right)
        } else {
            current.left = insert(data, current.left)
        }
        return current
    }

    override fun find(data: T, current: BinaryTreeNode<T>?): BinaryTreeNode<T>? {
        if (current === null)
            return null

        return if (current.data == data) {
            current
        } else {
            val left = find(data, current.left)
            if (left !== null) {
                left
            } else {
                find(data, current.right)
            }
        }
    }

    override fun delete(data: T, current: BinaryTreeNode<T>?): BinaryTreeNode<T>? {
        if (current === null)
            return current

        when {
            data < current.data -> current.left = delete(data, current.left)
            data > current.data -> current.right = delete(data, current.right)
            else -> {
                when {
                    current.left === null -> return current.right
                    current.right === null -> return current.left
                    else -> {
                        current.data = minValue(current.right!!)
                        current.right = delete(current.data, current.right)
                    }
                }
            }
        }

        return current
    }

    private fun minValue(from: BinaryTreeNode<T>): T {
        return from.left?.let {
            minValue(it)
        } ?: from.data
    }
}
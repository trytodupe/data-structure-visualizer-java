package com.trytodupe.operation;

import com.trytodupe.operation.array.atomic.ArrayCopyAtomicOperation;
import com.trytodupe.operation.array.atomic.ArrayResizeAtomicOperation;
import com.trytodupe.operation.array.atomic.ArrayWriteAtomicOperation;
import com.trytodupe.operation.avltree.atomic.AVLTreeLeftRotateAtomicOperation;
import com.trytodupe.operation.avltree.atomic.AVLTreeRightRotateAtomicOperation;
import com.trytodupe.operation.avltree.atomic.AVLTreeUpdateNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeAddNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeConnectNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeDisconnectNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeRemoveNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeReplaceSubtreeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeSwapValueAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeUpdateValueAtomicOperation;
import com.trytodupe.operation.huffmantree.atomic.HuffmanTreeAddAtomicOperation;
import com.trytodupe.operation.huffmantree.atomic.HuffmanTreeMergeAtomicOperation;
import com.trytodupe.operation.stack.atomic.StackPopAtomicOperation;
import com.trytodupe.operation.stack.atomic.StackPushAtomicOperation;
import com.trytodupe.operation.utils.VisualizePathAtomicOperation;

public interface IOperationVisitor {
    void visit(ArrayCopyAtomicOperation op);
    void visit(ArrayResizeAtomicOperation op);
    void visit(ArrayWriteAtomicOperation op);
    void visit(AVLTreeLeftRotateAtomicOperation<?> op);
    void visit(AVLTreeRightRotateAtomicOperation<?> op);
    void visit(AVLTreeUpdateNodeAtomicOperation<?> op);
    void visit(BinaryTreeAddNodeAtomicOperation<?> op);
    void visit(BinaryTreeConnectNodeAtomicOperation<?> op);
    void visit(BinaryTreeDisconnectNodeAtomicOperation<?> op);
    void visit(BinaryTreeRemoveNodeAtomicOperation<?> op);
    void visit(BinaryTreeReplaceSubtreeAtomicOperation<?> op);
    void visit(BinaryTreeSwapValueAtomicOperation<?> op);
    void visit(BinaryTreeUpdateValueAtomicOperation<?> op);
    void visit(HuffmanTreeAddAtomicOperation<?> op);
    void visit(HuffmanTreeMergeAtomicOperation<?> op);
    void visit(StackPopAtomicOperation op);
    void visit(StackPushAtomicOperation op);
    void visit(VisualizePathAtomicOperation op);
}

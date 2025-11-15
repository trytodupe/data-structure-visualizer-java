# Data Structure Visualizer Operation Design

## 1. AtomicOperation（原子操作）

**概念**  
- 数据结构上的最小操作单元，不可再拆。  
- 能够执行 (`execute`) 和撤销 (`undo`) 操作。  
- 通常携带旧值以支持 undo。  
- 每个 atomic operation 对应的数据结构实例是泛型 `T extends DataStructure`，或节点类型泛型如 `AVLTreeNode<E>`。

**特点**
- **最小粒度**，示例：
  - `ArrayWriteAtomicOperation(index, value)`  
  - `BinaryTreeAddNodeAtomicOperation(nodeUUID)`  
  - `AVLTreeLeftRotateAtomicOperation(pivotUUID)`  
- **可逆性**：保存 old state，实现 undo。  
- **可序列化**：存储操作，用于动画回放或日志。  

**职责**
- 修改数据结构状态。  
- 提供撤销能力。  
- 提供可视化描述 (`draw()` / `getDescription()`)。

---

## 2. UserOperation（用户操作）

**概念**  
- 一组 atomic operation 的组合，表示一个用户意图的操作。  
- 可以是单步操作，也可以是多步复杂操作。

**特点**
- **由 atomic operation 组成**：
  - `ArrayInsertUserOperation` → `ArrayResizeAtomicOperation` + `ArrayCopyAtomicOperation` + `ArrayWriteAtomicOperation`  
  - `AVLTreeBalanceUserOperation` → `AVLTreeRotateAtomicOperation` + `AVLTreeUpdateNodeAtomicOperation`  
- **延迟生成 atomic operation**：
  - 因为 atomic operation 可能依赖于当前树状态（如 AVL 树高度）。  
  - UserOperation 可先存参数（插入节点 UUID），在 `buildOperations()` 或执行阶段生成 atomic operation。  
- **提供 undo/redo**：依赖 atomic operation 的 undo。  

**职责**
- 将 atomic 操作组合成可理解的用户操作。  
- 管理执行顺序。  
- 在构建时进行逻辑校验（索引合法性、节点存在性）。  
- 可序列化为 `OperationDescriptor`（存储参数和类型，而非实时 atomic operation 对象）。

---

## 3. Composite / ChildOperation（复合操作）

**概念**  
- 嵌套 UserOperation，将一系列 UserOperation 当作一个整体执行。  
- 用于复杂数据结构操作或分阶段动画。

**特点**
- **组合多步用户操作**：
  - `BinarySearchTreeInitCompositeOperation` → 多个 `BinarySearchTreeInsertUserOperation`  
  - `AVLTreeInitCompositeOperation` → `BinarySearchTreeInsertUserOperation` + `AVLTreeUpdateTreeUserOperation` + `AVLTreeBalanceUserOperation`  
- **保持原子性**：undo/redo 按顺序执行 atomic operation。  
- **延迟生成**：子 UserOperation 生成 atomic operation 后，再统一执行。  

**职责**
- 组合多个 user operation 成一个逻辑操作。  
- 提供统一 execute/undo 接口。  
- 可序列化为 operation descriptor（仅存参数和类型）。

---

## 4. 操作层次关系图

+------------------------+
| UserOperation | <-- 可序列化 + 延迟生成 atomic
+------------------------+
| List<AtomicOperation> |
| buildOperations() |
| execute()/undo() |
+------------------------+
|
v
+------------------------+
| AtomicOperation | <-- 最小操作单元，可逆
+------------------------+
| execute()/undo() |
| oldValue/newValue |
| draw()/getDescription()|
+------------------------+

CompositeUserOperation (可选)
└─ 包含多个 UserOperation
└─ 每个 UserOperation 又包含自己的 AtomicOperation

---

## 5. 设计原则

1. **单一职责**：
   - AtomicOperation：只负责修改数据结构  
   - UserOperation：只负责组合 atomic 操作并管理逻辑  
   - CompositeUserOperation：只负责组合 UserOperation  

2. **可逆性**：
   - 每个 atomic operation 保存足够信息实现 undo  

3. **延迟构建 atomic**：
   - 用户操作可存参数，atomic operation 在执行前生成，保证序列化/动画一致性  

4. **泛型与类型安全**：
   - `AtomicOperation<T extends DataStructure>`  
   - `UserOperation<T extends DataStructure>`  
   - 可复用操作逻辑，同时保持类型检查  

5. **可序列化**：
   - UserOperation 和 CompositeUserOperaion 操作都可以序列化到 descriptor
   - AtomicOperation 可以从 UserOperation 生成，没有适配序列化
   - 重建时通过 descriptor + `postDeserialize()` 恢复实例绑定
### Binary Tree

#### Atomic Operations

```
(Binary Tree) ←- (5)
    |   ↑
(3) |   | (2)
    ↓   |
 (Temp Node)
    |   ↑
(4) |   | (1)
    ↓   |
```

1. Add Node ()
2. Connect Node (parent (null for root), childType)
3. Disconnect Node (parent, child)
4. Remove Node ()
5. Update Value (node, newValue)

#### User Operations

1. Insert (parent (null for root), value, childType)
   - (1) Add Node
   - (2) Connect Node
   - (5) Update Value
2. Delete (parent, child)
   - (3) Disconnect Node
   - (4) Remove Node
3. Update Value (node, newValue)
   - (5) Update Value
   - 
#### Composite User Operations

1. Init (value[])
   - multiple Insert user operations




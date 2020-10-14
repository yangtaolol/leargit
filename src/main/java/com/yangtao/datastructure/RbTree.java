package com.yangtao.datastructure.tree;

import java.util.LinkedList;
import java.util.List;

/**
 * @author yangtao-053
 * @Date 2020/10/12
 */
@SuppressWarnings("unchecked")
public class RbTree<T> {


	private Node<T> root;

	private int size = 0;


	public boolean contains(T v) {
		return root != null && find(v) != null;
	}

	public int size() {
		return size;
	}

	/**
	 * 测试用，判断当前树是否满足红黑树规范
	 *
	 * @return
	 */
	public boolean isRbTree() {
		if (root == null) {
			return true;
		}
		if (root.color == Color.RED) {
			System.out.println("root is red");
			return false;
		}
		List<Node<T>> leafes = new LinkedList<>();
		findLeaf(root, leafes);
		int num = -1;
		//遍历路径比较黑节点数目以及是否有红色节点相连
		for (Node<T> leaf : leafes) {
			int i = 0;
			Node<T> ch = leaf;
			while (ch != null) {
				if (ch.p != null && ch.color == Color.RED && ch.p.color == Color.RED) {
					return false;
				}
				if (ch.color == Color.BLACK) {
					i++;
				}
				ch = ch.p;
			}
			if (num == -1) {
				num = i;
			} else if (num != i) {
				return false;
			}
		}
		return true;
	}

	private void findLeaf(Node<T> node, List<Node<T>> leafes) {
		if (node.l != null) {
			findLeaf(node.l, leafes);
		}
		if (node.l == null && node.r == null) {
			leafes.add(node);
		}
		if (node.r != null) {
			findLeaf(node.r, leafes);
		}

	}

	/**
	 * 添加元素
	 *
	 * @param v
	 */
	public void add(T v) {
		//如果没有根节点直接创建一个黑色的根节点
		if (root == null) {
			root = new Node<>(v);
			size++;
			return;
		}
		Node<T> p = root;
		Node<T> inserted;
		//插入节点
		while (true) {
			if (compareTo(v, p) > 0) {
				if (p.r == null) {
					inserted = new Node<>(v, Color.RED);
					p.r = inserted;
					inserted.p = p;
					break;
				}
				p = p.r;
			} else if (compareTo(v, p) < 0) {
				if (p.l == null) {
					inserted = new Node<>(v, Color.RED);
					p.l = inserted;
					inserted.p = p;
					break;
				}
				p = p.l;
			} else {
				return;
			}
		}
		//平衡节点
		while (p.p != null && p.color == Color.RED) {
			Node<T> brother = p.p.l == p ? p.p.r : p.p.l;
			//以下关系均为插入节点的亲属关系
			//如果叔叔节点为红色，将叔叔节点和父节点涂黑祖父节点涂红，然后将祖父节点看作插入节点继续作平衡
			if (brother != null && brother.color == Color.RED) {
				brother.color = Color.BLACK;
				p.color = Color.BLACK;
				inserted = p.p;
				inserted.color = Color.RED;
				p = inserted.p;
				if (p == null) {
					break;
				}
				//如果叔叔节点不是红色，通过旋转给叔叔节点所在路径一个红色节点
			} else {
				Node gp = p.p.p;
				if (p == p.p.l) {
					if (inserted == p.l) {
						inserted = rrRotate(p.p, p);
					} else {
						inserted = lrRotate(p.p, p, inserted);
					}
				} else {
					if (inserted == p.r) {
						inserted = llRotate(p.p, p);
					} else {
						inserted = rlRotate(p.p, p, inserted);
					}
				}
				fixRelationShip(inserted);
				inserted.color = Color.BLACK;
				inserted.l.color = Color.RED;
				inserted.r.color = Color.RED;
				break;
			}
		}
		root = inserted.p == null ? inserted : root;
		root.color = Color.BLACK;
		size++;
	}

	/**
	 * 删除元素
	 *
	 * @param value
	 * @return
	 */
	public boolean delete(T value) {
		Node<T> delete = find(value);
		while (delete != null && getChNum(delete) != 0) {
			int chNum = getChNum(delete);
			Node<T> successor = delete;
			if (chNum == 1) {
				successor = delete.l == null ? delete.r : delete.l;
			} else if (chNum == 2) {
				successor = getSuccessor(delete);
			}
			delete.v = successor.v;
			delete = successor;
		}
		//没找到需要删除的节点
		if (delete == null) {
			return false;
		}
		//删除
		if (delete.p == null) {
			root = null;
		} else if (isLeft(delete)) {
			delete.p.l = null;
		} else {
			delete.p.r = null;
		}
		//平衡
		if (delete.color == Color.BLACK && delete.p != null) {
			balance(delete);
		}
		size--;
		return true;
	}

	private Node<T> find(T value) {
		Node<T> node = root;
		while (node != null) {
			int result = compareTo(value, node);
			if (result == 0) {
				return node;
			}
			if (result < 0) {
				node = node.l;
			} else {
				node = node.r;
			}
		}
		return null;
	}

	/**
	 * 为删除作平衡
	 *
	 * @param delete
	 */
	private void balance(Node<T> delete) {
		Node<T> node = delete.p;
		Color color = node.color;
		Node<T> ch = null;
		if (compareTo(delete.v, node) < 0) {
			Node<T> brother = node.r;
			//以下亲属关系均被删除节点的亲属关系
			//兄弟节点为黑色
			if (brother.color == Color.BLACK) {
				//兄弟有红色右节点
				if (brother.r != null && brother.r.color == Color.RED) {
					ch = llRotate(node, node.r);
					//兄弟有红色左节点
				} else if (brother.l != null && brother.l.color == Color.RED) {
					ch = rlRotate(node, node.r, node.r.l);
					//兄弟节点无黑色子节点
				} else {
					brother.color = Color.RED;
					//父亲为黑节点
					if (node.color == Color.BLACK && node.p != null) {
						balance(node);
					}
					node.color = Color.BLACK;
				}

			} else {
				Node p = llRotate(node, node.r);
				p.color = Color.BLACK;
				p.l.color = Color.RED;
				fixRelationShip(p);
				balance(delete);
			}
		} else {
			Node<T> brother = node.l;
			if (brother.color == Color.BLACK) {
				if (brother.l != null && brother.l.color == Color.RED) {
					ch = rrRotate(node, node.l);
				} else if (brother.r != null && brother.r.color == Color.RED) {
					ch = lrRotate(node, node.l, node.l.r);
				} else {
					brother.color = Color.RED;
					if (node.color == Color.BLACK && node.p != null) {
						balance(node);
					}
					node.color = Color.BLACK;
				}
			} else {
				Node<T> p = rrRotate(node, node.l);
				p.color = Color.BLACK;
				p.r.color = Color.RED;
				fixRelationShip(p);
				balance(delete);

			}
		}
		if (ch != null) {
			fixRelationShip(ch);
			ch.color = color;
			ch.l.color = ch.r.color = Color.BLACK;
		}
	}


	private void fixRelationShip(Node<T> ch) {
		if (ch.p == null) {
			root = ch;
		} else if (compareTo(ch.v, ch.p) < 0) {
			ch.p.l = ch;
		} else {
			ch.p.r = ch;
		}
	}

	public Node<T> getSuccessor(Node<T> curr) {
		Node<T> successor;
		if (curr.r != null) {
			Node<T> ch = curr.r;
			while (ch.l != null) {
				ch = ch.l;
			}
			successor = ch;
		} else {
			Node<T> p = curr.p;
			Node<T> ch = curr;
			while (p != null && p.l != ch) {
				ch = p;
				p = p.p;
			}
			successor = p;
		}
		return successor;
	}

	private boolean isLeft(Node<T> ch) {
		return ch.p.l == ch;
	}


	private int getChNum(Node node) {
		int chNum = 0;
		if (node.l != null) {
			chNum++;
		}
		if (node.r != null) {
			chNum++;
		}
		return chNum;
	}

	private Node<T> rrRotate(Node k1, Node k2) {
		k2.p = k1.p;
		k1.p = k2;
		k1.l = k2.r;
		if (k1.l != null) {
			k1.l.p = k1;
		}
		k2.r = k1;
		return k2;
	}

	private Node<T> llRotate(Node k1, Node k2) {
		k2.p = k1.p;
		k1.p = k2;
		k1.r = k2.l;
		if (k1.r != null) {
			k1.r.p = k1;
		}
		k2.l = k1;
		return k2;
	}

	private Node<T> lrRotate(Node k1, Node k2, Node k3) {
		k2 = llRotate(k2, k3);
		return rrRotate(k1, k2);
	}

	private Node<T> rlRotate(Node k1, Node k2, Node k3) {
		k2 = rrRotate(k2, k3);
		return llRotate(k1, k2);
	}


	private int compareTo(T v, Node<T> p) {
		if (!(v instanceof Comparable)) {
			throw new IllegalArgumentException(String.format("the Type:[%s] dose not support comparison operations",
					v.getClass().getName()));
		}
		return ((Comparable) v).compareTo(p.v);
	}

	private static class Node<T> {
		private Node p;

		private Node l;

		private Node r;

		private T v;

		private Color color = Color.BLACK;

		Node(T v) {
			if (v == null) {
				throw new NullPointerException("value can not be null!");
			}
			this.v = v;
		}

		Node(T v, Color color) {
			this(v);
			this.color = color;
		}
	}

	private enum Color {
		BLACK(),
		RED
	}
}

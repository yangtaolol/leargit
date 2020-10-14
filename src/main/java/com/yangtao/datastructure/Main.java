package com.yangtao.datastructure.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 100; i++) {
			test();
		}
	}

	//测试用例
	private static void test() throws InterruptedException {
		Random random = new Random();
		int size = random.nextInt(1001) + 1000;
		RbTree<Integer> tree = new RbTree<>();
		RbTree<Object> tree1 = new RbTree<>();
		List<Integer> list = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			int num = random.nextInt(10001);
			if (list.contains(num)) {
				continue;
			}
			tree.add(num);
			if (!tree.isRbTree()) {
				System.out.println(String.format("error add :%d", num));
				TimeUnit.SECONDS.sleep(2);
			}
			tree1.add(num);
			list.add(num);
		}
		Collections.shuffle(list);
		for (Integer num : list) {
			tree.delete(num);
			if (!tree.isRbTree()) {
				System.out.println(String.format("error: %d", num));
				TimeUnit.SECONDS.sleep(2);
			}
			tree1.delete(num);
		}

	}
}

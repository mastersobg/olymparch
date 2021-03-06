import java.io.*;
import java.util.*;

import static java.lang.Math.*;

public class Solution {

	BufferedReader in;
	StringTokenizer st;
	PrintWriter out;

	static class Pair {

		int x, y;

		public Pair(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;
			Pair p = (Pair) other;
			return x == p.x && y == p.y;
		}

		@Override
		public int hashCode() {
			return x * 31 + y;
		}

		@Override
		public String toString() {
			return "[x = " + x + ", y = " + y + "]";
		}

	}

	final Pair[] DIR = { new Pair(-1, 0), new Pair(0, -1), new Pair(1, 0),
			new Pair(0, 1) };
	final String[] DIR_NAME = { "UP", "LEFT", "DOWN", "RIGHT" };

	class Map {

		List<String> moves = new ArrayList<String>();
		boolean writeMoves = true;

		int[][] v;
		Pair p;

		Map(int n) {
			v = new int[n][n];
			p = new Pair(0, 0);
		}

		List<Integer> getState() {
			List<Integer> ret = new ArrayList<Integer>();
			for (int i = 0; i < v.length; ++i)
				for (int j = 0; j < v[i].length; ++j)
					ret.add(v[i][j]);
			return ret;
		}

		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;
			Map m = (Map) other;
			return Arrays.deepEquals(v, m.v);
		}

		@Override
		public int hashCode() {
			return Arrays.deepHashCode(v);
		}

		boolean isOk() {
			int prev = -1;
			for (int i = 0; i < v.length; ++i)
				for (int j = 0; j < v[i].length; ++j) {
					if (v[i][j] <= prev) {
						return false;
					}
					prev = v[i][j];
				}
			return true;
		}

		void move(Pair d) {
			assert d.x == 0 || d.y == 0 : "" + d.x + " " + d.y;
			assert abs(d.x) == 1 || abs(d.y) == 1 : "" + d.x + " " + d.y;
			try {
				v[p.x][p.y] = v[p.x + d.x][p.y + d.y];
			} catch (ArrayIndexOutOfBoundsException e) {
				int k = 1;
			}
			v[p.x + d.x][p.y + d.y] = 0;
			p.x += d.x;
			p.y += d.y;
			if (writeMoves) {
				for (int i = 0; i < DIR.length; ++i)
					if (DIR[i].equals(d)) {
						moves.add(DIR_NAME[i]);
						break;
					}
			}
			// dbg(this);
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < v.length; ++i) {
				for (int j = 0; j < v[i].length; ++j) {
					if (v[i][j] < 10)
						sb.append(' ');
					sb.append(v[i][j] + " ");
				}
				sb.append('\n');
			}
			return sb.toString();
		}

		void moveFreeTo(Pair dest, List<Pair> closed) {
			int n = v.length;
			int[][] d = new int[n][n];
			for (int i = 0; i < n; ++i)
				Arrays.fill(d[i], -1);
			Pair[] q = new Pair[n * n];
			int b = 0, e = 0;
			d[p.x][p.y] = -5;
			HashSet<Pair> closedSet = new HashSet<Pair>(closed);
			gl: for (q[e++] = new Pair(p.x, p.y); b < e; ++b) {
				Pair cur = q[b];
				for (int i = 0; i < DIR.length; ++i) {
					int nx = cur.x + DIR[i].x;
					int ny = cur.y + DIR[i].y;
					Pair np = new Pair(nx, ny);
					if (nx >= 0 && nx < n && ny >= 0 && ny < n
							&& !closedSet.contains(np) && d[nx][ny] == -1) {
						d[nx][ny] = (i + 2) % DIR.length;
						q[e++] = np;
						if (np.equals(dest)) {
							break gl;
						}
					}
				}
			}
			int curX = dest.x, curY = dest.y;
			List<Integer> mvs = new ArrayList<Integer>();
			while (d[curX][curY] != -5) {
				int value = d[curX][curY];
				mvs.add((value + 2) % 4);
				if (value == -1) {
					int k = 1;
					++k;
				}
				curX = curX + DIR[value].x;
				curY = curY + DIR[value].y;
			}
			Collections.reverse(mvs);
			for (int move : mvs) {
				move(DIR[move]);
			}
		}

		void shift(Pair d) {
			moveFreeTo(new Pair(p.x + d.x, p.y + d.y), new ArrayList<Pair>(0));
		}

		void makeMove(int dir, int angle) {
			move(DIR[dir]);
			dir = turn(dir, angle);
			move(DIR[dir]);
			dir = turn(dir, angle);
			move(DIR[dir]);
			move(DIR[dir]);
			dir = turn(dir, angle);
			move(DIR[dir]);
		}

		int turn(int d, int a) {
			return (d + a + 4) % 4;
		}

		Pair find(int value) {
			int n = v.length;
			for (int i = 0; i < n; ++i)
				for (int j = 0; j < n; ++j)
					if (v[i][j] == value)
						return new Pair(i, j);
			throw new IllegalArgumentException("value = " + value
					+ " not found");
		}
	}

	// over down
	Pair moveRight(Map map, Pair p, int iters, List<Pair> c) {
		if (iters == 0)
			return p;
		List<Pair> closed = new ArrayList<Pair>(c);
		closed.add(p);
		map.moveFreeTo(new Pair(p.x, p.y + 1), closed);
		for (int it = 0; it < iters - 1; ++it) {
			if (p.x == map.v.length - 1)
				map.makeMove(1, -1);
			else
				map.makeMove(1, 1);
		}
		map.shift(new Pair(0, -1));
		return new Pair(p.x, p.y + iters);
	}

	// over left
	Pair moveDown(Map map, Pair p, int iters, List<Pair> c) {
		if (iters == 0)
			return p;
		List<Pair> closed = new ArrayList<Pair>(c);
		closed.add(p);
		map.moveFreeTo(new Pair(p.x + 1, p.y), closed);
		for (int it = 0; it < iters - 1; ++it) {
			if (p.y == 0)
				map.makeMove(0, -1);
			else
				map.makeMove(0, 1);
		}
		map.shift(new Pair(-1, 0));
		return new Pair(p.x + iters, p.y);
	}

	// over left
	Pair moveUp(Map map, Pair p, int iters, List<Pair> c) {
		if (iters == 0)
			return p;
		List<Pair> closed = new ArrayList<Pair>(c);
		closed.add(p);
		map.moveFreeTo(new Pair(p.x - 1, p.y), closed);
		for (int it = 0; it < iters - 1; ++it) {
			if (p.y == 0)
				map.makeMove(2, 1);
			else
				map.makeMove(2, -1);
		}
		map.shift(new Pair(1, 0));
		return new Pair(p.x - iters, p.y);
	}

	// over up
	Pair moveLeft(Map map, Pair p, int iters, List<Pair> c) {
		if (iters == 0)
			return p;
		List<Pair> closed = new ArrayList<Pair>(c);
		closed.add(p);
		map.moveFreeTo(new Pair(p.x, p.y - 1), closed);
		for (int it = 0; it < iters - 1; ++it) {
			if (p.x == 0)
				map.makeMove(3, -1);
			else
				map.makeMove(3, 1);
		}
		map.shift(new Pair(0, 1));
		return new Pair(p.x, p.y - iters);
	}

	void rightColumn(Map map, int row, int value, List<Pair> closed,
			boolean last) {
		int n = map.v.length;
		List<Pair> nlist = new ArrayList(closed);
		if (last)
			nlist.add(new Pair(n - 2, n - 1));

		map.moveFreeTo(new Pair(0, 0), nlist);
		Pair p = map.find(value);
		// if (p.y > 1)
		// p = moveLeft(map, p, p.y - 1, nlist);
		// if (p.x > 1)
		// p = moveUp(map, p, p.x - 1, nlist);
		// if (p.x == 0)
		// p = moveDown(map, p, 1, nlist);
		// if (p.y == 0)
		// p = moveRight(map, p, 1, nlist);

		if (p.y == 0)
			p = moveRight(map, p, 1, nlist);
		if (p.y == n - 1)
			p = moveLeft(map, p, 1, nlist);
		if (p.x > row)
			p = moveUp(map, p, (p.x - row), nlist);
		if (p.x < row)
			p = moveDown(map, p, (row - p.x), nlist);
		moveRight(map, p, (n - p.y - 1), closed);
	}

	void downColumn(Map map, int col, int value, List<Pair> closed, boolean last) {
		int n = map.v.length;
		List<Pair> nlist = new ArrayList(closed);
		if (last)
			nlist.add(new Pair(n - 1, 1));

		map.moveFreeTo(new Pair(0, 0), nlist);
		Pair p = map.find(value);
		// if (p.x > 1)
		// p = moveUp(map, p, p.x - 1, nlist);
		// if (p.y > 1)
		// p = moveLeft(map, p, p.y - 1, nlist);
		// if (p.x == 0)
		// p = moveDown(map, p, 1, nlist);
		// if (p.y == 0)
		// p = moveRight(map, p, 1, nlist);

		if (p.x == 0)
			p = moveDown(map, p, 1, nlist);
		if (p.x == n - 1)
			p = moveUp(map, p, 1, nlist);
		if (p.y < col)
			p = moveRight(map, p, (col - p.y), nlist);
		if (p.y > col)
			p = moveLeft(map, p, (p.y - col), nlist);
		moveDown(map, p, (n - p.x - 1), closed);
	}

	List<String> solve(Map map, int N) {
		int n = map.v.length;
		if (n == 3)
			return brute(map);
		List<Pair> closed = new ArrayList<Pair>();
		for (int i = 0; i < n - 2; ++i) {
			int row = i;
			int value = i * N + n - 1;
			rightColumn(map, row, value, closed, false);
			closed.add(new Pair(i, n - 1));
		}
		Pair last = map.find((n - 2) * N + n - 1);
		moveLeft(map, last, last.y, closed);
		rightColumn(map, n - 2, (n - 1) * N + n - 1, closed, false);
		rightColumn(map, n - 2, (n - 2) * N + n - 1, closed, true);
		closed.add(new Pair(n - 2, n - 1));
		closed.add(new Pair(n - 1, n - 1));
		// dbg(closed);

		for (int i = n - 2; i > 1; --i) {
			int col = i;
			int value = (n - 1) * N + i;
			downColumn(map, col, value, closed, false);
			closed.add(new Pair(n - 1, col));
			// dbg(value);
		}
		last = map.find((n - 1) * N + 1);
		moveUp(map, last, last.x, closed);
		downColumn(map, 1, (n - 1) * N, closed, false);
		downColumn(map, 1, (n - 1) * N + 1, closed, true);
		Map subMap = createSubMap(map, n - 1);
		List<String> result = new ArrayList<String>(map.moves);
		// dbg(map);
		result.addAll(solve(subMap, N));
		return result;
	}

	boolean movesNull;

	List<String> brute(Map map) {
		// dbg(map);
		map.writeMoves = false;
		HashSet<List<Integer>> was = new HashSet<List<Integer>>();
		List<Integer> moves = rec(map, was);
		if (moves == null) {
			movesNull = true;
			return new ArrayList<String>();
		}
		// dbg(was.size());
		Collections.reverse(moves);
		map.writeMoves = true;
		map.moves = new ArrayList<String>();
		for (int d : moves)
			map.move(DIR[d]);
		return map.moves;
	}

	void test(Map map) {
		HashSet<List<Integer>> was = new HashSet<List<Integer>>();
		go(map.v, was);
		dbg(was.size());
	}

	List<Integer> rec(Map map, HashSet<List<Integer>> was) {
		if (map.isOk()) {
			return new ArrayList<Integer>();
		}
		List<Integer> state = map.getState();
		if (was.contains(state)) {
			return null;
		}
		// dbg(was.size());
		was.add(state);
		int n = map.v.length;
		for (int i = 0; i < DIR.length; ++i) {
			int nx = map.p.x + DIR[i].x;
			int ny = map.p.y + DIR[i].y;
			if (nx >= 0 && nx < n && ny >= 0 && ny < n) {
				map.move(DIR[i]);
				List<Integer> ret = rec(map, was);
				map.move(DIR[(i + 2) % 4]);
				if (ret != null) {
					ret.add(i);
					return ret;
				}
			}
		}
		return null;
	}

	void go(int[][] v, HashSet<List<Integer>> was) {
		ArrayList<Integer> state = new ArrayList<Integer>();
		int n = v.length;
		int x = -1, y = -1;
		for (int i = 0; i < n; ++i)
			for (int j = 0; j < n; ++j) {
				state.add(v[i][j]);
				if (v[i][j] == 0) {
					x = i;
					y = j;
				}
			}
		if (was.contains(state))
			return;
		was.add(state);
		for (int i = -1; i < 2; ++i)
			for (int j = -1; j < 2; ++j)
				if (abs(i + j) == 1) {
					int nx = x + i;
					int ny = y + j;
					if (nx >= 0 && nx < n && ny >= 0 && ny < n) {
						swap(v, x, y, nx, ny);
						go(v, was);
						swap(v, x, y, nx, ny);
					}
				}
	}

	void swap(int[][] v, int x, int y, int nx, int ny) {
		int t = v[x][y];
		v[x][y] = v[nx][ny];
		v[nx][ny] = t;
	}

	Map createSubMap(Map map, int size) {
		Map ret = new Map(size);
		for (int i = 0; i < size; ++i)
			for (int j = 0; j < size; ++j)
				ret.v[i][j] = map.v[i][j];
		ret.p = new Pair(map.p.x, map.p.y);
		return ret;
	}

	void solve() throws IOException {
//		while (true) {
			Map map = read();
			dbg("init map = \n", map);
			//
			// int[] vs = new int[map.v.length * map.v.length];
			// for (int i = 0; i < map.v.length; ++i)
			// for (int j = 0; j < map.v.length; ++j)
			// vs[i * map.v.length + j] = map.v[i][j];
			// dbg(hasSolution(vs));

			Map init = createSubMap(map, map.v.length);
			List<String> ret = null;
			// try {
			movesNull = false;
			ret = solve(map, map.v.length);
//			if (movesNull)
//				continue;
			// } catch (NullPointerException e) {
			// continue;
			// }
			out.println(ret.size());
			for (String s : ret) {
				 out.println(s);
				for (int i = 0; i < DIR.length; ++i)
					if (DIR_NAME[i].equals(s)) {
						init.move(DIR[i]);
						break;
					}
			}
//			if (!init.isOk()) {
//				dbg("FAILED");
//				dbg(init);
//				throw new RuntimeException();
//			}
//		}
		// dbg(init);
	}

	Map read() throws IOException {
		int n = ni();
		Map map = new Map(n);
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j) {
				int a = ni();
				map.v[i][j] = a;
				if (a == 0)
					map.p = new Pair(i, j);
			}
		}
		return map;
	}

	Random rnd = new Random(1234567890L);

	Map generateMap() {
		Map map = new Map(5);
		int[] vs = new int[25];
		HashSet<Integer> was = new HashSet<Integer>();
		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 5; ++j) {
				int value = rnd.nextInt(25);
				while (was.contains(value))
					value = rnd.nextInt(25);
				was.add(value);
				map.v[i][j] = value;
				vs[i * 5 + j] = value;
				if (value == 0) {
					map.p.x = i;
					map.p.y = j;
				}
			}
		}
		return hasSolution(vs) ? map : generateMap();
	}

	boolean hasSolution(int[] a) {
		int inv = 0;
		for (int i = 0; i < a.length; ++i)
			if (a[i] != 0)
				for (int j = 0; j < i; ++j)
					if (a[j] > a[i])
						++inv;
		for (int i = 0; i < a.length; ++i)
			if (a[i] == 0)
				inv += 1 + i / 4;

		return (inv & 1) == 1 ? true : false;
	}

	boolean DEBUG = false;

	void dbg(Object... args) {
		if (!DEBUG)
			return;
		for (Object o : args)
			System.err.print(o + " ");
		System.err.println();
	}

	public void run() throws IOException {
		Locale.setDefault(Locale.US);
		in = new BufferedReader(new InputStreamReader(System.in));
		out = new PrintWriter(System.out);
		solve();
		in.close();
		out.close();
	}

	String ns() throws IOException {
		while (st == null || !st.hasMoreTokens())
			st = new StringTokenizer(in.readLine());
		return st.nextToken();
	}

	int ni() throws IOException {
		return Integer.valueOf(ns());
	}

	long nl() throws IOException {
		return Long.valueOf(ns());
	}

	double nd() throws IOException {
		return Double.valueOf(ns());
	}

	public static void main(String[] args) throws IOException {
		new Solution().run();
	}
}

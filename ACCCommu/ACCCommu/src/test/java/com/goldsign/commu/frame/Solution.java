package com.goldsign.commu.frame;

import com.goldsign.commu.frame.util.ByteGenUtil;
import com.goldsign.commu.frame.util.DateHelper;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author:
 * @params:
 * @Date:{10:03} {2020/5/15}
 **/
class Solution {

    private List<Integer>[] edges;
    private int[] DFN;
    private int[] LOW;
    private boolean[] visited;
    private List<List<Integer>> ans;
    private int t;

    public List<List<Integer>> criticalConnections(int n, List<List<Integer>> connections) {
        this.edges = new ArrayList[n];
        this.DFN = new int[n];
        this.LOW = new int[n];
        this.visited = new boolean[n];
        this.ans = new ArrayList<>();
        this.t = 0;
        for (int i = 0; i < n; i++) {
            edges[i] = new ArrayList<>();
        }
        for (List<Integer> conn : connections) {
            int n1 = conn.get(0), n2 = conn.get(1);
            edges[n1].add(n2);
            edges[n2].add(n1);
        }
        tarjan(0, -1);
        return ans;
    }

    public void tarjan(int cur, int pre) {
        t++;
        DFN[cur] = t;
        LOW[cur] = t;
        visited[cur] = true;
        for (int node : edges[cur]) {
            if (node == pre) continue;
            if (!visited[node]) {
                tarjan(node, cur);
                LOW[cur] = Math.min(LOW[cur], LOW[node]);
                if (LOW[node] > DFN[cur]) {
                    List<Integer> list = new ArrayList<>();
                    list.add(cur);
                    list.add(node);
                    ans.add(list);
                }
            } else {
                LOW[cur] = Math.min(LOW[cur], DFN[node]);
            }
        }

    }

    //给定一个整数数组和一个整数 k，你需要找到该数组中和为 k 的连续的子数组的个数。数组的长度为 [1, 20,000]。
//数组中元素的范围是 [-1000, 1000] ，且整数 k 的范围是 [-1e7, 1e7]。
    public int subarraySum(int[] nums, int k) {
        int total = 0;
        for (int i = 0; i < nums.length; i++) {
            int sum = 0;
            for (int j = i; j < nums.length; j++) {
                sum = sum + nums[j];
                if (sum == k) {
                    total++;
                }
            }
        }
        System.out.println(total);
        return total;
    }

    //在字符串 s 中找出第一个只出现一次的字符。如果没有，返回一个单空格。 s 只包含小写字母。
    public char firstUniqChar(String s) {
//        List<Character> list = new ArrayList<Character>();
        Map<Character, Integer> map = new HashMap<Character, Integer>();
        if (s.equals(" "))
            return ' ';
        char[] ch = s.toCharArray();
        for (char c : ch) {
            if (!map.containsKey(c))
                map.put(c, 0);
            else
                map.put(c, 1);
        }
        System.out.println(map.toString());
        for (char cc : ch) {
            if (map.get(cc) == 1)
                return cc;
        }
        return ' ';
    }

    public int missingNumber(int[] nums) {
        int need = 0;
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i + 1] - nums[i] == 2) need = nums[i] + 1;
        }
        System.out.println(need);
        return need;
    }

    //给你一个整数数组 nums ，请你找出数组中乘积最大的连续子数组（该子数组中至少包含一个数字），并返回该子数组所对应的乘积
    public int maxProduct(int[] nums) {
        if (nums.length == 0)
            return 0;
        //动态规划 dp[][0] max dp[][1] min
        int dp[][] = new int[nums.length][2];
        dp[0][0] = nums[0];
        dp[0][1] = nums[0];
        //以nums[i] 正负区分 并区分dp[][]正负
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] >= 0) {
                dp[i][0] = Math.max(nums[i], dp[i - 1][0] * nums[i]);
                dp[i][1] = Math.min(nums[i], dp[i - 1][1] * nums[i]);
            }
            if (nums[i] < 0) {
                dp[i][0] = Math.max(nums[i], dp[i - 1][1] * nums[i]);
                dp[i][1] = Math.min(nums[i], dp[i - 1][0] * nums[i]);
            }
        }
        int res = dp[0][0];
        for (int i = 0; i < nums.length; i++) {
            res = Math.max(res, dp[i][0]);
        }
        System.out.println("res=" + res);
        return res;
    }

    //假设按照升序排序的数组在预先未知的某个点上进行了旋转。
    //
    //( 例如，数组 [0,0,1,2,2,5,6] 可能变为 [2,5,6,0,0,1,2] )。
    //
    //编写一个函数来判断给定的目标值是否存在于数组中。若存在返回 true，否则返回 false。
    public boolean search(int[] nums, int target) {
        boolean result = false;
        Arrays.sort(nums);
        int a = Arrays.binarySearch(nums, 0, nums.length, target);
        if (a >= 0)
            result = true;

        System.out.println(result);
        return result;
    }

    //   给定一个非空字符串 s，最多删除一个字符。判断是否能成为回文字符串(超时 )
    public boolean validPalindrome(String s) {
        boolean result = false;
        StringBuilder reverse = new StringBuilder(s).reverse();
        if (!reverse.toString().equals(s)) {
            int len = s.length();
            for (int i = 0; i < len; i++) {
                StringBuilder sb = new StringBuilder(s);
                String temp = sb.deleteCharAt(i).toString();
                StringBuilder tmp1 = sb.reverse();
                if (tmp1.toString().equals(temp)) {
                    result = true;
                    break;
                }
            }
        } else {
            result = true;
        }

        return result;
    }

    //        public boolean validPalindrome(String s) {
//            int low = 0, high = s.length() - 1;
//            while (low < high) {
//                char c1 = s.charAt(low), c2 = s.charAt(high);
//                if (c1 == c2) {
//                    low++;
//                    high--;
//                } else {
//                    boolean flag1 = true, flag2 = true;
//                    for (int i = low, j = high - 1; i < j; i++, j--) {
//                        char c3 = s.charAt(i), c4 = s.charAt(j);
//                        if (c3 != c4) {
//                            flag1 = false;
//                            break;
//                        }
//                    }
//                    for (int i = low + 1, j = high; i < j; i++, j--) {
//                        char c3 = s.charAt(i), c4 = s.charAt(j);
//                        if (c3 != c4) {
//                            flag2 = false;
//                            break;
//                        }
//                    }
//                    return flag1 || flag2;
//                }
//            }
//            return true;
//        }
//    你是一个专业的小偷，计划偷窃沿街的房屋。每间房内都藏有一定的现金，影响你偷窃的唯一制约因素就是相邻的房屋装有相互连通的防盗系统，如果两间相邻的房屋在同一晚上被小偷闯入，系统会自动报警。
//
//    给定一个代表每个房屋存放金额的非负整数数组，计算你 不触动警报装置的情况下 ，一夜之内能够偷窃到的最高金额。
//    动态规划
//        public int rob(int[] nums) {
//
//        }
    private static final String VOWELS = "aeiou";

    public int findTheLongestSubstring(String s) {
        Map<Integer, Integer> map = new HashMap<>();
        int size = s.length();
        int state = 0; // (00000)
        int maxSize = 0;
        map.putIfAbsent(0, -1);
        for (int i = 0; i < size; i++) {
            for (int k = 0; k < VOWELS.length(); k++) {
                if (s.charAt(i) == VOWELS.charAt(k)) {
                    state ^= (1 << (VOWELS.length() - k - 1));
                    break;
                }
            }
            if (map.containsKey(state)) {
                maxSize = Math.max(maxSize, i - map.get(state));
            }
            map.putIfAbsent(state, i);
        }

        return maxSize;
    }

    //给定由若干 0 和 1 组成的数组 A。我们定义 N_i：从 A[0] 到 A[i] 的第 i 个子数组被解释为一个二进制数（从最高有效位到最低有效位）。
    //
    //返回布尔值列表 answer，只有当 N_i 可以被 5 整除时，答案 answer[i] 为 true，否则为 false。
//    public List<Boolean> prefixesDivBy5(int[] A) {
//        int len = A.length;
//        List<Boolean> ans = new ArrayList<>(len);
//        for(int i=len-2;i<len;i++){
//            int[] tmp =new int[i+1];
//            for(int j=0;j<i+1;j++){
//                tmp[j]=A[j];
//            }
//            ans.add(myTest(tmp));
//        }
//        return ans;
//    }
    //超出int 范围  位数左移
    public Boolean myTest(int A[]) {
        int total = 0;
        for (int i = 0; i < A.length; i++) {
            total += A[i] * (Math.pow(2, A.length - i - 1));
        }
        if (total % 5 == 0)
            return true;
        else
            return false;
    }

    public List<Boolean> prefixesDivBy5(int[] A) {
        int num = 0;
        List<Boolean> ans = new ArrayList<>();
        for (int index = 0; index < A.length; index++) {
            num = (num * 2 + A[index]) % 5;
            if (num == 0) ans.add(true);
            else ans.add(false);
        }
        return ans;
    }


    public void test(StringBuilder stringBuilder) {
//        StringBuilder sb = stringBuilder.deleteCharAt(0);
        StringBuilder sb = stringBuilder.replace(0, 0, "");
        System.out.println(sb.toString());
    }

    //驼峰式匹配
//    public List<Boolean> camelMatch(String[] queries, String pattern) {
//        List<Boolean> ans = new ArrayList<>();
//        char[] pt=pattern.toCharArray();
//
//
//    }
    //最大回文子串
    public String longestPalindrome(String s) {
        String max = "";
        int len = s.length();
        List<String> maxList = foundMax(0, len, s);
        for (String stored : maxList)
            if (stored.length() > max.length()) {
                max = stored;
            }
        return max;
    }

    public List<String> foundMax(int start, int end, String s) {
        List<String> tmpMax = new ArrayList<>();
        StringBuilder tmp = new StringBuilder(s);
        if (tmp.reverse().toString().equals(s)) {
            tmpMax.add(s);
            return tmpMax;
        }
        for (int i = start; i < end; i++) {
            for (int j = i; j < end; j++) {
                String tmpString = s.substring(i, j);
                if (i == j) {
                    tmpString = String.valueOf(s.charAt(i));
                }
                StringBuilder tmp1 = new StringBuilder(tmpString);
                if (tmp1.reverse().toString().equals(tmpString))
                    tmpMax.add(tmpString);
            }
        }
        return tmpMax;
    }
    // public class TreeNode {
    //    int val;
    //    TreeNode left;
    //    TreeNode right;
    //    TreeNode(int x) { val = x; }
    //}
//    给定一个包含 n + 1 个整数的数组 nums，其数字都在 1 到 n 之间（包括 1 和 n），可知至少存在一个重复的整数。假设只有一个重复的整数，找出这个重复的数

    public int findDuplicate(int[] nums) {
        int duplicate = 0;
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            if (map.containsKey(num)) {
                duplicate = num;
                break;
            }
            map.put(num, 1);
        }
        return duplicate;
    }
//    给定一个表示分数加减运算表达式的字符串，你需要返回一个字符串形式的计算结果。 这个结果应该是不可约分的分数，即最简分数。 如果最终结果是一个整数，例如 2，你需要将它转换成分数形式，其分母为 1。所以在上述例子中, 2 应该被转换为 2/1。

    //    public String fractionAddition(String expression) {
//        expression.split("\\+");
//    }
//最大公约数
    public int gcd(int a, int b) {
        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    //三步问题。有个小孩正在上楼梯，楼梯有n阶台阶，小孩一次可以上1阶、2阶或3阶。实现一种方法，计算小孩有多少种上楼梯的方式。结果可能很大，你需要对结果模1000000007。
    public int waysToStep(int n) {
        int dp[] = new int[n + 1];
        dp[1] = 1;
        dp[2] = 2;
        dp[3] = 4;
        for (int i = 4; i < n + 1; i++) {
            dp[i] = dp[i - 1] + dp[i - 2] + dp[i - 3];
        }
        return dp[n];
    }

    //最小公倍数 等于乘积除以最大公约数 a*b/gcd(a,b)
//给你一个数组 candies 和一个整数 extraCandies ，其中 candies[i] 代表第 i 个孩子拥有的糖果数目。
//
//对每一个孩子，检查是否存在一种方案，将额外的 extraCandies 个糖果分配给孩子们之后，此孩子有 最多 的糖果。注意，允许有多个孩子同时拥有 最多 的糖果数目。
    public List<Boolean> kidsWithCandies(int[] candies, int extraCandies) {
        int tempMax = 0;
        List<Boolean> IsMaxCandy = new ArrayList<>();
        for (int candy : candies) {
            tempMax = Math.max(candy, tempMax);
        }
        for (int candy : candies) {
//            if((candy+extraCandies)>tempMax){
//                IsMaxCandy.add(true);
//            }
            IsMaxCandy.add(((candy + extraCandies) >= tempMax) ? true : false);
        }
        return IsMaxCandy;
    }
//    给你一个长度为 n 的整数数组 nums，其中 n > 1，返回输出数组 output ，其中 output[i] 等于 nums 中除 nums[i] 之外其余各元素的乘积。

    public int[] productExceptSelf(int[] nums) {
        int left = 1;
        int right = 1;
        int len = nums.length;
        int[] output = new int[len];
        for (int i = 0; i < len; i++) {
            output[i] = left;
            left *= nums[i];
        }
        for (int j = len - 1; j >= 0; j--) {
            output[j] *= right;
            right *= nums[j];
        }
        return output;
    }

    //求 1+2+...+n ，要求不能使用乘除法、for、while、if、else、switch、case等关键字及条件判断语句（A?B:C）。
    public int sumNums(int n) {
        int result = 0;
        return result;
    }
//    爱丽丝参与一个大致基于纸牌游戏 “21点” 规则的游戏，描述如下：
//
//    爱丽丝以 0 分开始，并在她的得分少于 K 分时抽取数字。 抽取时，她从 [1, W] 的范围中随机获得一个整数作为分数进行累计，其中 W 是整数。 每次抽取都是独立的，其结果具有相同的概率。
//
//    当爱丽丝获得不少于 K 分时，她就停止抽取数字。 爱丽丝的分数不超过 N 的概率是多少？

    //    public double new21Game(int N, int K, int W) {
//
//    }
//    输入一个矩阵，按照从外向里以顺时针的顺序依次打印出每一个数字
//    按层模拟
    public int[] spiralOrder(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return new int[0];
        }
        int rows = matrix.length;
        int columns = matrix[0].length;
        int[] order = new int[rows * columns];
        int index = 0;
        int left = 0, right = columns - 1, top = 0, bottom = rows - 1;
        while (left <= right && top <= bottom) {
            for (int column = left; column <= right; column++) {
                order[index++] = matrix[top][column];
            }
            for (int row = top + 1; row <= bottom; row++) {
                order[index++] = matrix[row][right];
            }
            //注意数组越界
//            if (left < right && top < bottom) {
            for (int column = right - 1; column > left; column--) {
                order[index++] = matrix[bottom][column];
            }
            for (int row = bottom; row > top; row--) {
                order[index++] = matrix[row][left];
            }
            //           }
            left++;
            right--;
            top++;
            bottom--;
        }
        for (int a : order) {
            System.out.println(a);
        }
        return order;
    }
    public int numWays(int n) {
        int dp[] =new int[n+1];
        dp[0]=0;
        dp[1]=1;
        dp[2]=2;
        for(int i=3;i<n+1;i++){
            dp[i]=dp[i-1]+dp[i-2];
        }
        return dp[n];
    }

    //    public int[] spiralOrder(int[][] matrix) {
//        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
//            return new int[0];
//        }
//        int rows = matrix.length, columns = matrix[0].length;
//        int[] order = new int[rows * columns];
//        int index = 0;
//        int left = 0, right = columns - 1, top = 0, bottom = rows - 1;
//        while (left <= right && top <= bottom) {
//            for (int column = left; column <= right; column++) {
//                order[index++] = matrix[top][column];
//            }
//            for (int row = top + 1; row <= bottom; row++) {
//                order[index++] = matrix[row][right];
//            }
////            if (left < right && top < bottom) {
//                for (int column = right - 1; column > left; column--) {
//                    order[index++] = matrix[bottom][column];
//                }
//                for (int row = bottom-1; row > top; row--) {
//                    order[index++] = matrix[row][left];
//                }
////            }
//            left++;
//            right--;
//            top++;
//            bottom--;
//        }
//        for(int a:order){
//            System.out.println(a);
//        }
//        return order;
//    }
    //并查集 算法
    public boolean equationsPossible(String[] equations) {
        int length = equations.length;
        int[] parent = new int[26];
        for (int i = 0; i < 26; i++) {
            parent[i] = i;
        }
        for (String str : equations) {
            if (str.charAt(1) == '=') {
                int index1 = str.charAt(0) - 'a';
                int index2 = str.charAt(3) - 'a';
                union(parent, index1, index2);
            }
        }
        for (String str : equations) {
            if (str.charAt(1) == '!') {
                int index1 = str.charAt(0) - 'a';
                int index2 = str.charAt(3) - 'a';
                if (find(parent, index1) == find(parent, index2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void union(int[] parent, int index1, int index2) {
        parent[find(parent, index1)] = find(parent, index2);
    }

    public int find(int[] parent, int index) {
        while (parent[index] != index) {
            parent[index] = parent[parent[index]];
            index = parent[index];
        }
        return index;
    }

    //    给定一个数字，我们按照如下规则把它翻译为字符串：0 翻译成 “a” ，1 翻译成 “b”，……，11 翻译成 “l”，……，25 翻译成 “z”。一个数字可能有多个翻译。请编程实现一个函数，用来计算一个数字有多少种不同的翻译方法。
//    public int translateNum(int num) {
//        if(num<=0)
//            return 0;
//    }
    //不转换为字符串  判断整数是否为回文数  取余数（获取每一位数字 然后乘10 + 对10取整的数）
    public boolean isPalindrome(int x) {
        if (x < 0 || (x % 10 == 0 && x != 0)) {
            return false;
        }
        int y = 0;
        int z = x;
        while (z > 0) {
            int yushu = z % 10;
            y = y * 10 + yushu;
            z = z / 10;
        }
        return y == x;
    }
//根据每日 气温 列表，请重新生成一个列表，对应位置的输出是需要再等待多久温度才会升高超过该日的天数。如果之后都不会升高，请在该位置用 0 来代替。
//
//例如，给定一个列表 temperatures = [73, 74, 75, 71, 69, 72, 76, 73]，你的输出应该是 [1, 1, 4, 2, 1, 1, 0, 0]。
//
//提示：气温 列表长度的范围是 [1, 30000]。每个气温的值的均为华氏度，都是在 [30, 100] 范围内的整数。

    public int[] dailyTemperatures(int[] T) {
        int length = T.length;
        int result[] = new int[length];
        for (int i = 0; i < length; i++) {
            for (int j = i + 1; j < length; j++) {
                if (T[j] > T[i]) {
                    result[i] = j - i;
                    break;
                }
            }
        }
        return result;
    }

    //    给你一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？请你找出所有满足条件且不重复的三元组。
//先排序  然后考虑如何去重  双指针循环
    public List<List<Integer>> threeSum(int[] nums) {
        int length = nums.length;
        List<List<Integer>> totallist = new ArrayList<>();
        for (int i = 0; i < length - 2; i++) {
            for (int j = i + 1; j < length - 1; j++) {
                for (int k = j + 1; k < length; k++) {
                    List<Integer> list = new ArrayList<>();

                    if (nums[i] + nums[j] + nums[k] == 0) {
                        list.add(nums[i]);
                        list.add(nums[j]);
                        list.add(nums[k]);
                    }
                    if (!list.isEmpty()) {
                        totallist.add(list);
                    }
                }
            }
        }
        return totallist;
    }

    //    编写一个函数来查找字符串数组中的最长公共前缀。
//
//    如果不存在公共前缀，返回空字符串 ""。
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0)
            return "";
        String prefix = strs[0];
        for (int i = 1; i < strs.length; i++) {
            prefix = findPrefix(prefix, strs[i]);
            if (prefix.length() == 0)
                break;
        }
        return prefix;
    }

    public String findPrefix(String prefix, String str) {
        int length = Math.min(prefix.length(), str.length());
        int index = 0;
        while (index < length && prefix.charAt(index) == str.charAt(index)) {
            index++;
        }
        return prefix.substring(0, index);
    }
//    给定一个包括 n 个整数的数组 nums 和 一个目标值 target。找出 nums 中的三个整数，使得它们的和与 target 最接近。返回这三个数的和。假定每组输入只存在唯一答案。

    //public int threeSumClosest(int[] nums, int target) {
    //    Map<Integer,Integer> diff = new HashMap();
    //    //diff.putIfAbsent(0,0);
    //    int diffClose=0;
    //    for(int i=0;i<nums.length-2;i++){
    //        for(int j=i+1;j<nums.length-1;j++){
    //            for(int k=j+1;k<nums.length;k++) {
    //                diffClose = (nums[i] + nums[j] + nums[k] - target);
    //                if (diffClose < 0) {
    //                    diffClose = -diffClose;
    //                }
    //                if (diff.isEmpty()) {
    //                    diff.put(0, diffClose);
    //                } else {
    //                    if (diffClose < diff.get(0)) {
    //                        diff.put(0, diffClose);
    //                    }
    //                }
    //            }
    //        }
    //    }
    //    return diff.get(0);
    //}
    //在未排序的数组中找到第 k 个最大的元素。请注意，你需要找的是数组排序后的第 k 个最大的元素，而不是第 k 个不同的元素。
    public int findKthLargest(int[] nums, int k) {
        int max = 0;
        int length = nums.length;
        if (1 > k || k > length) {
            return 0;
        }
        //排序
        Arrays.sort(nums);
        System.out.println(nums[length - k]);
        return nums[length - k];
    }

    //最长公共子串
    //public int findLength(int[] A, int[] B) {
    //    int maxLenth = 0;
    //    int circleLenth = Math.max(A.length,B.length);
    //    int begin=0,end=0;
    //    while(begin<circleLenth){
    //        for(int i=0;i<circleLenth;i++){
    //            if(A[i]==B[begin])
    //        }
    //    }
    //}
    //给定一个 n x n 矩阵，其中每行和每列元素均按升序排序，找到矩阵中第 k 小的元素。
    //请注意，它是排序后的第 k 小元素，而不是第 k 个不同的元素。
    public int kthSmallest(int[][] matrix, int k) {
        if (matrix.length == 0)
            return 0;
        int n = matrix[0].length;
        int nums[] = new int[n * n];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = matrix[i / n][i % n];
        }
        Arrays.sort(nums);
        for (int num : nums) {
            System.out.println(num);
        }
        return nums[k];
    }
    //将一个按照升序排列的有序数组，转换为一棵高度平衡二叉搜索树。
    //本题中，一个高度平衡二叉树是指一个二叉树每个节点 的左右两个子树的高度差的绝对值不超过 1。


//public class TreeNode1 {
//    int val;
//    TreeNode1 left;
//    TreeNode1 right;
//    TreeNode1(int x) { val = x; }
//     }
//
//    public TreeNode1 sortedArrayToBST(int[] nums) {
//        return helper(nums, 0, nums.length - 1);
//    }
//
//    public TreeNode1 helper(int[] nums, int left, int right) {
//        if (left > right) {
//            return null;
//        }
//
//        // 总是选择中间位置左边的数字作为根节点
//        int mid = (left + right) / 2;
//
//        TreeNode1 root = new TreeNode1(nums[mid]);
//        root.left = helper(nums, left, mid - 1);
//        root.right = helper(nums, mid + 1, right);
//        return root;
//    }

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    //   给定一个二叉树和一个目标和，判断该树中是否存在根节点到叶子节点的路径，这条路径上所有节点值相加等于目标和。
    public boolean hasPathSum(TreeNode root, int sum) {
        if (root == null) {
            return false;
        }
        if (root.left == null && root.right == null) {
            return sum == root.val;
        }
        return hasPathSum(root.left, sum - root.val) || hasPathSum(root.right, sum - root.val);
    }
    //你正在使用一堆木板建造跳水板。有两种类型的木板，其中长度较短的木板长度为shorter，长度较长的木板长度为longer。你必须正好使用k块木板。编写一个方法，生成跳水板所有可能的长度。

    public int[] divingBoard(int shorter, int longer, int k) {
        if (shorter <= 0 || shorter > longer || longer <= 0 || k < 0 || k > 10000) {
            return null;
        }

        List list = new ArrayList();
        int maxLength = 0;
        for (int i = 0; i <= k; i++) {
            maxLength = i * longer + (k - i) * shorter;
            if (!list.contains(maxLength)) {
                list.add(maxLength);
            }
        }
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = (int) list.get(i);
        }
        Arrays.sort(result);
        return result;
    }

    /**
     * Definition for a binary tree node.
     * public class TreeNode {
     * int val;
     * TreeNode left;
     * TreeNode right;
     * TreeNode(int x) { val = x; }
     * }
     */
    //class Solution {
    public int minDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }

        if (root.left == null && root.right == null) {
            return 1;
        }

        int min_depth = Integer.MAX_VALUE;
        if (root.left != null) {
            min_depth = Math.min(minDepth(root.left), min_depth);
        }
        if (root.right != null) {
            min_depth = Math.min(minDepth(root.right), min_depth);
        }

        return min_depth + 1;
    }


    //}
    //
    //哦，不！你不小心把一个长篇文章中的空格、标点都删掉了，并且大写也弄成了小写。像句子"I reset the computer. It still didn’t boot!"已经变成了"iresetthecomputeritstilldidntboot"。在处理标点符号和大小写之前，你得先把它断成词语。当然了，你有一本厚厚的词典dictionary，不过，有些词没在词典里。假设文章用sentence表示，设计一个算法，把文章断开，要求未识别的字符最少，返回未识别的字符数。
    //
    //注意：本题相对原题稍作改动，只需返回未识别的字符数
//字典树

    //public int respace(String[] dictionary, String sentence) {
    //    int dLength = dictionary.length;
    //    int sLength = sentence.length();
    //    int noneLength = 0;
    //    for(int i=0;i<sLength;i++){
    //            char temp =sentence.charAt(i);
    //            for(int j=0;j<dLength;j++){
    //                if(){
    //
    //                }
    //            }
    //    }
    //}

    //给定两个数组，编写一个函数来计算它们的交集。

    public int[] intersect(int[] nums1, int[] nums2) {
        Arrays.sort(nums1);
        Arrays.sort(nums2);
        int length1 = nums1.length, length2 = nums2.length;
        int[] intersection = new int[Math.min(length1, length2)];
        int index1 = 0, index2 = 0, index = 0;
        while (index1 < length1 && index2 < length2) {
            if (nums1[index1] < nums2[index2]) {
                index1++;
            } else if (nums1[index1] > nums2[index2]) {
                index2++;
            } else {
                intersection[index] = nums1[index1];
                index1++;
                index2++;
                index++;
            }
        }
        return Arrays.copyOfRange(intersection, 0, index);


    }

    //public boolean repeatedSubstringPattern(String s) {
    //
    //}

    //给定一个整型数组, 你的任务是找到所有该数组的递增子序列，递增子序列的长度至少是2。
    //输入: [4, 6, 7, 7]
    //输出: [[4, 6], [4, 7], [4, 6, 7], [4, 6, 7, 7], [6, 7], [6, 7, 7], [7,7], [4,7,7]]

    //public List<List<Integer>> findSubsequences(int[] nums) {
    //    List list  = new ArrayList();
    //    List<Integer> subList = new ArrayList<Integer>();
    //    int length = nums.length;
    //    Arrays.sort(nums);
    //
    //}
    //给定一个仅包含数字 2-9 的字符串，返回所有它能表示的字母组合。
    //
    //给出数字到字母的映射如下（与电话按键相同）。注意 1 不对应任何字母。
    //public List<String> letterCombinations(String digits) {
    //    char[] test = digits.toCharArray();
    //    for(test:t){
    //        if(t =='1')
    //            return null;
    //    }
    //    Map<Character,String> map = new HashMap<Character, String>(8);
    //    map.put('2',"abc");
    //    map.put('3',"def");
    //    map.put('4',"ghl");
    //    map.put('5',"jkl");
    //    map.put('6',"mno");
    //    map.put('7',"pqrs");
    //    map.put('8',"tuv");
    //    map.put('9',"wxyz");
    //
    //}


    public List<String> letterCombinations(String digits) {
        List<String> combinations = new ArrayList<String>();
        if (digits.length() == 0) {
            return combinations;
        }
        Map<Character, String> phoneMap = new HashMap<Character, String>() {{
            put('2', "abc");
            put('3', "def");
            put('4', "ghi");
            put('5', "jkl");
            put('6', "mno");
            put('7', "pqrs");
            put('8', "tuv");
            put('9', "wxyz");
        }};
        backtrack(combinations, phoneMap, digits, 0, new StringBuffer());
        return combinations;
    }

    public void backtrack(List<String> combinations, Map<Character, String> phoneMap, String digits, int index, StringBuffer combination) {
        if (index == digits.length()) {
            combinations.add(combination.toString());
        } else {
            char digit = digits.charAt(index);
            String letters = phoneMap.get(digit);
            int lettersCount = letters.length();
            for (int i = 0; i < lettersCount; i++) {
                combination.append(letters.charAt(i));
                backtrack(combinations, phoneMap, digits, index + 1, combination);
                combination.deleteCharAt(index);
            }
        }
    }

    //在二维平面上，有一个机器人从原点 (0, 0) 开始。给出它的移动顺序，判断这个机器人在完成移动后是否在 (0, 0) 处结束。
    //
    //移动顺序由字符串表示。字符 move[i] 表示其第 i 次移动。机器人的有效动作有 R（右），L（左），U（上）和 D（下）。如果机器人在完成所有动作后返回原点，则返回 true。否则，返回 false。
    //输入: "UD"
    //输出: true
    //解释：机器人向上移动一次，然后向下移动一次。所有动作都具有相同的幅度，因此它最终回到它开始的原点。因此，我们返回 true。
    public boolean judgeCircle(String moves) {
        boolean result = false;
        char[] move = moves.toCharArray();
        int upNums = 0, dNums = 0, lNums = 0, rNums = 0;
        for (int i = 0; i < move.length; i++) {
            switch (move[i]) {
                case 'U':
                    upNums++;
                    break;
                case 'D':
                    dNums++;
                    break;
                case 'L':
                    lNums++;
                    break;
                case 'R':
                    rNums++;
                    break;
                default:
                    break;
            }
        }
        if (upNums == dNums && lNums == rNums) {
            result = true;
        }
        return result;
    }

    public String reverseWords(String s) {
        StringBuilder result = new StringBuilder();
        String[] temp = s.split(" ");
        for( String str : temp){
            StringBuffer temp1 = new StringBuffer(str);
            result.append(temp1.reverse().toString()+" ");
        }
        return result.toString().substring(0,s.length()-1);
    }

    //有 N 个房间，开始时你位于 0 号房间。每个房间有不同的号码：0，1，2，...，N-1，并且房间里可能有一些钥匙能使你进入下一个房间。
    //
    //在形式上，对于每个房间 i 都有一个钥匙列表 rooms[i]，每个钥匙 rooms[i][j] 由 [0,1，...，N-1] 中的一个整数表示，其中 N = rooms.length。 钥匙 rooms[i][j] = v 可以打开编号为 v 的房间。
    //
    //最初，除 0 号房间外的其余所有房间都被锁住。
    //
    //你可以自由地在房间之间来回走动。
    //
    //如果能进入每个房间返回 true，否则返回 false。
    //public boolean canVisitAllRooms(List<List<Integer>> rooms) {
    //    boolean result = false ;
    //    if(rooms.isEmpty() || rooms.size()<1 ||rooms.size()>1000){
    //        return result;
    //    }
    //    for(int i=0;i<rooms.size();i++){
    //        List<Integer> singleRoom = rooms.get(i);
    //        if(singleRoom.contains(i)){
    //            result = false;
    //            break;
    //        }
    //    }
    //
    //    return result;
    //}


        // 判断字符数组能不能转换字符串
//        boolean flag=false;
//        public boolean isNumeric(char[] str) {
//            if(str.length==0 || str==null){
//                return flag;
//            }
////将字符数组转换成字符串
//            String strs=String.valueOf(str);
//            try{
//                new BigDecimal(strs);
//                flag=true;
//            }catch(Exception e){
//                //转换出错
//                return flag;
//            }
//            return flag;
//        }
    public boolean isNUmberic(String string){
        boolean flag = false ;
        try{
            new BigDecimal(string);
            flag =true;
        }catch (Exception e){
            return flag;
        }
        return flag;
    }
    //编写一个函数来验证输入的字符串是否是有效的 IPv4 或 IPv6 地址。
    //IPv4 地址由十进制数和点来表示，每个地址包含4个十进制数，其范围为 0 - 255， 用(".")分割。比如，172.16.254.1；
    //同时，IPv4 地址内的数不会以 0 开头。比如，地址 172.16.254.01 是不合法的。
    //IPv6 地址由8组16进制的数字来表示，每组表示 16 比特。这些组数字通过 (":")分割。比如,  2001:0db8:85a3:0000:0000:8a2e:0370:7334 是一个有效的地址。而且，我们可以加入一些以 0 开头的数字，字母可以使用大写，也可以是小写。所以， 2001:db8:85a3:0:0:8A2E:0370:7334 也是一个有效的 IPv6 address地址 (即，忽略 0 开头，忽略大小写)。
    //然而，我们不能因为某个组的值为 0，而使用一个空的组，以至于出现 (::) 的情况。 比如， 2001:0db8:85a3::8A2E:0370:7334 是无效的 IPv6 地址。
    //同时，在 IPv6 地址中，多余的 0 也是不被允许的。比如， 02001:0db8:85a3:0000:0000:8a2e:0370:7334 是无效的。
    public String validIPAddress(String IP) {
        final String[] enums = {"IPv4","IPv6","Neither"};
        String  result = "";
        String  regex1=" ^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
        String  regex2="^([\\da-fA-F]{1,4}:){6}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^::([\\da-fA-F]{1,4}:){0,4}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:):([\\da-fA-F]{1,4}:){0,3}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){2}:([\\da-fA-F]{1,4}:){0,2}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){3}:([\\da-fA-F]{1,4}:){0,1}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){4}:((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}$|^:((:[\\da-fA-F]{1,4}){1,6}|:)$|^[\\da-fA-F]{1,4}:((:[\\da-fA-F]{1,4}){1,5}|:)$|^([\\da-fA-F]{1,4}:){2}((:[\\da-fA-F]{1,4}){1,4}|:)$|^([\\da-fA-F]{1,4}:){3}((:[\\da-fA-F]{1,4}){1,3}|:)$|^([\\da-fA-F]{1,4}:){4}((:[\\da-fA-F]{1,4}){1,2}|:)$|^([\\da-fA-F]{1,4}:){5}:([\\da-fA-F]{1,4})?$|^([\\da-fA-F]{1,4}:){6}:$";
        if(Pattern.matches(regex1,IP)){
            result = enums[0];
        }else if(Pattern.matches(regex2,IP)){
            result = enums[1];
        }else{
            result = enums[2];
        }

        return result;
    }


        int N;              // 记录n
        char[][] board;     // 模拟棋盘
        List<List<String>> schemes = new LinkedList<>();    // 存放合法方案
        public List<List<String>> solveNQueens(int n) {
            N = n;
            board = new char[N][N];
            for (char[] line : board) {     // 初始化
                Arrays.fill(line, '.');
            }
            backtrack(0);   // 从第 1 行开始放皇后
            return schemes;
        }

        public void backtrack(int r) {
            // 结束条件
            if (r == N) {
                // 添加方案
                List<String> scheme = new LinkedList<>();
                for (char[] line : board) {
                    scheme.add(String.valueOf(line));
                }
                schemes.add(scheme);
                return;
            }
            // 选择列表
            for (int j = 0; j < N; j++) {
                if (isValid(r, j)) {             // 合法才能选
                    board[r][j] = 'Q';          // 选择
                    backtrack(r + 1);           // 往下一层搜索
                    board[r][j] = '.';          // 撤销选择，以免影响同一行其他位置的摆放
                }
            }
        }

        public boolean isValid(int x, int y) {
            // 同一行
            for (int j = 0; j < y; j++) {
                if (board[x][j] == 'Q') {
                    return false;
                }
            }
            // 同一列
            for (int i = 0; i < x; i++) {
                if (board[i][y] == 'Q') {
                    return false;
                }
            }
            // 主对角线
            for (int i = x - 1, j = y - 1; i >= 0 && j >= 0; i--, j--) {
                if (board[i][j] == 'Q') {
                    return false;
                }
            }
            // 副对角线
            for (int i = x - 1, j = y + 1; i >= 0 && j < N; i--, j++) {
                if (board[i][j] == 'Q') {
                    return false;
                }
            }
            return true;
        }
    //给定一个二叉树，返回所有从根节点到叶子节点的路径。
    //
    //说明: 叶子节点是指没有子节点的节点。
    public List<String> binaryTreePaths(TreeNode root) {
        List<String> paths = new ArrayList<String>();
        constructPaths(root, "", paths);
        return paths;
    }

    public void constructPaths(TreeNode root, String path, List<String> paths) {
        if (root != null) {
            StringBuffer pathSB = new StringBuffer(path);
            pathSB.append(Integer.toString(root.val));
            if (root.left == null && root.right == null) {  // 当前节点是叶子节点
                paths.add(pathSB.toString());  // 把路径加入到答案中
            } else {
                pathSB.append("->");  // 当前节点不是叶子节点，继续递归遍历
                constructPaths(root.left, pathSB.toString(), paths);
                constructPaths(root.right, pathSB.toString(), paths);
            }
        }
    }
    //给出集合 [1,2,3,…,n]，其所有元素共有 n! 种排列。
    //按大小顺序列出所有排列情况，并一一标记，当 n = 3 时, 所有排列如下：
    //        "123"
    //        "132"
    //        "213"
    //        "231"
    //        "312"
    //        "321"
    //给定 n 和 k，返回第 k 个排列。
    //说明：
    //给定 n 的范围是 [1, 9]。
    //给定 k 的范围是[1,  n!]。
    public String getPermutation(int n, int k) {
        int[] factorial = new int[n];
        factorial[0] = 1;
        for (int i = 1; i < n; ++i) {
            factorial[i] = factorial[i - 1] * i;
        }

        --k;
        StringBuffer ans = new StringBuffer();
        int[] valid = new int[n + 1];
        Arrays.fill(valid, 1);
        for (int i = 1; i <= n; ++i) {
            int order = k / factorial[n - i] + 1;
            for (int j = 1; j <= n; ++j) {
                order -= valid[j];
                if (order == 0) {
                    ans.append(j);
                    valid[j] = 0;
                    break;
                }
            }
            k %= factorial[n - i];
        }
        return ans.toString();
    }

    //public int[] topKFrequent(int[] nums, int k) {
    //
    //}
    List<Integer> temp = new ArrayList<Integer>();
    //List<List<Integer>> ans1 = new ArrayList<List<Integer>>();

    public List<List<Integer>> combine(int n, int k) {
        dfs(1, n, k);
        return ans;
    }

    public void dfs(int cur, int n, int k) {
        // 剪枝：temp 长度加上区间 [cur, n] 的长度小于 k，不可能构造出长度为 k 的 temp
        if (temp.size() + (n - cur + 1) < k) {
            return;
        }
        // 记录合法的答案
        if (temp.size() == k) {
            ans.add(new ArrayList<Integer>(temp));
            return;
        }
        // 考虑选择当前位置
        temp.add(cur);
        dfs(cur + 1, n, k);
        temp.remove(temp.size() - 1);
        // 考虑不选择当前位置
        dfs(cur + 1, n, k);
    }
//20201014
    //public List<String> commonChars(String[] A) {
    //    List finalList = new ArrayList();
    //    String minLengthStr = "";
    //    for(String str:A){
    //        if(str.length()<minLengthStr.length()){
    //            minLengthStr=str;
    //        }
    //    }
    //    char[] bChar = minLengthStr.toCharArray();
    //    for(char s:bChar){
    //        boolean result = false;
    //        for(int i=0;i<A.length;i++){
    //
    //        }
    //    }
    //    return finalList;
    //}
//      set 不允许重复的值
    public boolean uniqueOccurrences(int[] arr) {
        Map<Integer, Integer> occur = new HashMap<Integer, Integer>();
        for (int x : arr) {
            occur.put(x, occur.getOrDefault(x, 0) + 1);
        }
        Set<Integer> times = new HashSet<Integer>();
        for (Map.Entry<Integer, Integer> x : occur.entrySet()) {
            times.add(x.getValue());
        }
        return times.size() == occur.size();
    }

    public int islandPerimeter(int[][] grid) {
        int sum = 0;
     //   int rowLength = grid[0].length;//列数
      //  int colLength= grid.length;//
        for(int i=0;i<grid.length;i++){
            for(int j=0;j<grid[i].length;j++){
                sum+=check(grid[i][j]);
            }
        }
        return sum;
    }
    //左移右移 判断四个方向 未完成
    public  int  check(int testGrid) {
        int result = 0 ;
        return result;
    }
//给定一个数组 nums，编写一个函数将所有 0 移动到数组的末尾，同时保持非零元素的相对顺序。
//双指针 --交换
        public void moveZeroes(int[] nums) {
            int indexNow = 0;
            int indexNum = 0;
            int m = nums.length;
            //indexNow  先赋值  在+1
            while(indexNum<m){
                if(nums[indexNum] != 0) {
                    nums[indexNow++] = nums[indexNum];
                }
                ++indexNum;
            }

            for(int i = indexNow; i < m; i++){
                nums[i] = 0;
            }
        }

    public static void main(String args[]) {

        Solution solution = new Solution();

        Set<String> set = new HashSet<String>();
 //       System.out.println(solution.getPermutation(4,9));
 //       System.out.println(solution.combine(4,2));
 //       System.out.println(System.currentTimeMillis());
 //       String current = DateHelper.dateToString(new Date());
 //       System.out.println(current);
 //       // HHMM
 //       String curTime = current.substring(8, 12);
 //       System.out.println(curTime);
 //       System.out.println(Integer.parseInt("2359"));
 //       ByteGenUtil.intToByte(2,4);
        int[] arr={0,1,0,3,12};
       solution.moveZeroes(arr);
    }
}


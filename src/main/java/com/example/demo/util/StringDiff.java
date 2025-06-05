package com.example.demo.util;

import java.util.ArrayList;
import java.util.List;

public class StringDiff {

    public static void main(String[] args) {
        String str1 = "This is the first string for comparison";
        String str2 = "This is the second string for diff example";

        List<DiffBlock> diffBlocks = findDifferences(str1, str2);

        System.out.println("String 1: " + str1);
        System.out.println("String 2: " + str2);
        System.out.println("\nDifferences:");

        for (DiffBlock block : diffBlocks) {
            System.out.printf("Position %d-%d: '%s' vs '%s'\n",
                    block.getStart(), block.getEnd(),
                    block.getText1(), block.getText2());
        }
    }

    public static List<DiffBlock> findDifferences(String str1, String str2) {
        List<DiffBlock> diffBlocks = new ArrayList<>();

        int minLength = Math.min(str1.length(), str2.length());
        int startDiff = -1;

        for (int i = 0; i < minLength; i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                if (startDiff == -1) {
                    startDiff = i;
                }
            } else {
                if (startDiff != -1) {
                    // 发现差异结束
                    diffBlocks.add(new DiffBlock(
                            startDiff,
                            i - 1,
                            str1.substring(startDiff, i),
                            str2.substring(startDiff, i)
                    ));
                    startDiff = -1;
                }
            }
        }

        // 处理字符串末尾的差异
        if (startDiff != -1) {
            diffBlocks.add(new DiffBlock(
                    startDiff,
                    minLength - 1,
                    str1.substring(startDiff, minLength),
                    str2.substring(startDiff, minLength)
            ));
        }

        // 处理一个字符串比另一个长的情况
        if (str1.length() != str2.length()) {
            int maxLength = Math.max(str1.length(), str2.length());
            String longerStr = str1.length() > str2.length() ? str1 : str2;
            String shorterStr = str1.length() > str2.length() ? str2 : str1;

            diffBlocks.add(new DiffBlock(
                    minLength,
                    maxLength - 1,
                    str1.length() > str2.length() ? str1.substring(minLength) : "",
                    str2.length() > str1.length() ? str2.substring(minLength) : ""
            ));
        }

        return diffBlocks;
    }

    static class DiffBlock {
        private int start;
        private int end;
        private String text1;
        private String text2;

        public DiffBlock(int start, int end, String text1, String text2) {
            this.start = start;
            this.end = end;
            this.text1 = text1;
            this.text2 = text2;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public String getText1() {
            return text1;
        }

        public String getText2() {
            return text2;
        }
    }
}

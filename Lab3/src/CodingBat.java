public class CodingBat {
    public static String startOz(String str) {
        String out = "";

        if (str.length() > 0 && str.charAt(0) == 'o') { out += 'o'; }
        if (str.length() > 1 && str.charAt(1) == 'z') { out += 'z'; }

        return out;
    }

    public static int diff21(int n) {
        if (n > 21) { return 2*Math.abs(21-n); }
        else { return Math.abs(21-n); }
    }

    public static int sum67(int[] nums) {
        int sum = 0;
        boolean skip = false;

        for (int i = 0; i < nums.length; i++) {
            int num = nums[i];

            if (skip == false && num == 6) { skip = true; }
            else if (skip == true && num == 7) { skip = false; }
            else if (skip == false) { sum += num; }
        }

        return sum;
    }

    public static String middleTwo(String str) {
        String out = "";

        out += str.charAt(str.length()/2-1);
        out += str.charAt(str.length()/2);

        return out;
    }
}

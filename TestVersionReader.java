public class TestVersionReader {
    public static void main(String[] args) {
        String version = mod.deplayer.coffeechat.GETGPV.getVersionFromGradleProperties();
        if (version != null) {
            System.out.println("成功读取版本号: " + version);
        } else {
            System.out.println("读取版本号失败");
        }
    }
}
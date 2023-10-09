package br.com.marketimobi.api.utils;

public class VerificarPastasSO {
	
	public static void main(String args[]) {
		String tmpDir = System.getProperty("java.io.tmpdir");
        System.out.println(tmpDir);
		
        String userHome = System.getProperty("user.home") + "\\marketimobi\\";
        System.out.println(userHome);
        
		String userDir = System.getProperty("user.dir") + "\\marketimobi\\";
		System.out.println(userDir);
    }

}

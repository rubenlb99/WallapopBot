/**
 * 
 */
package bot;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;



/**
 * @author ruben
 *
 */
public class Wallapop {
	private WebDriver driver;
	private String [] urls, graficas = { 
			"3070", "3060 ti", "3060" , "6700 xt"
	};
	private List<String> anunciosGuardados = new ArrayList<String>();
	
	private boolean stop;

	public Wallapop(boolean hide, String[] urls) throws InterruptedException {
		this.urls = urls;
		if(hide) {
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--headless");
			options.addArguments("--mute-audio");
			options.addArguments("--no-sandbox");
			driver = new ChromeDriver(options);
		}else {
			driver = new ChromeDriver();
		}	
		stop = false;
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		
	}
	
	public void preSniping() {
		
				
		for (int i = 0; i < urls.length; i++) {
			driver.navigate().to(urls[i]);
			
			for (int j = 1; j <= 6; j++) {
				try {													
					WebElement anuncio = driver.findElement(By.xpath("/html/body/tsl-root/tsl-public/div/div/tsl-search/div/tsl-search-layout/div/div[2]/div/tsl-public-item-card-list/div/a["+j+"]"));
					String titulo = anuncio.getAttribute("title") , url;
					
					anuncio.click();
					url = driver.getCurrentUrl();

					ArrayList<String> tabs2 = new ArrayList<String> (driver.getWindowHandles());
				    driver.switchTo().window(tabs2.get(1));
				    driver.close();
				    driver.switchTo().window(tabs2.get(0));
						
					System.out.println(titulo);
					
					if(titulo.contains(graficas[i]) && !anunciosGuardados.contains(url)) {
						anunciosGuardados.add(url);
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void sniping() {
		
		driver.navigate().to("https://es.wallapop.com/");
		driver.findElement(By.id("onetrust-accept-btn-handler")).click();
			
		while(true) {			
			
			for (int i = 0; i < urls.length; i++) {
				driver.navigate().to(urls[i]);
				
				for (int j = 1; j <= 6; j++) {
					try {													
						WebElement anuncio = driver.findElement(By.xpath("/html/body/tsl-root/tsl-public/div/div/tsl-search/div/tsl-search-layout/div/div[2]/div/tsl-public-item-card-list/div/a["+j+"]"));
						String titulo = anuncio.getAttribute("title") , url;
						
						anuncio.click();
						

						ArrayList<String> tabs2 = new ArrayList<String> (driver.getWindowHandles());
					    driver.switchTo().window(tabs2.get(1));
					    url = driver.getCurrentUrl();
					    driver.close();
					    driver.switchTo().window(tabs2.get(0));
							
						System.out.println(titulo);
						
						if(titulo.contains(graficas[i]) && !anunciosGuardados.contains(url)) {
							System.out.println("**** Nuevo anuncio  ----  Enviando mail");
							anunciosGuardados.add(url);
							Wallapop.sendMail("cocaballosgang@gmail.com", graficas[i], url, "ingenieriaweb2021@gmail.com", "davigjwawfbsrkvk");
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}

	}
	
	public static void sendMail(String destinatario, String asunto, String cuerpo, String remitente, String password) throws AddressException, MessagingException {
		Properties properties = new Properties(); // properties object contains host information
		
		String host = "smtp.gmail.com";
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(properties, new javax.mail.Authenticator(){
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(remitente, password);
			}
		});

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(remitente));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
			message.setSubject(asunto);
			message.setText(cuerpo);

			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
	      }

		
	}
	
	public static void main (String[] args) {
		String[] urls = { 
				"https://es.wallapop.com/app/search?keywords=3070&filters_source=quick_filters&longitude=-3.69196&latitude=40.41956&min_sale_price=200&max_sale_price=400&order_by=newest",
				"https://es.wallapop.com/app/search?keywords=3060%20ti&filters_source=quick_filters&longitude=-3.69196&latitude=40.41956&order_by=newest&min_sale_price=200&max_sale_price=400",
				"https://es.wallapop.com/app/search?keywords=3060&filters_source=search_box&longitude=-3.69196&latitude=40.41956&order_by=newest&min_sale_price=150&max_sale_price=300",
				"https://es.wallapop.com/app/search?keywords=6700%20xt&filters_source=search_box&longitude=-3.69196&latitude=40.41956&order_by=newest&min_sale_price=200&max_sale_price=400",
		};

		try {
			Wallapop w = new Wallapop(false, urls);
			w.sniping();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @param args
	 */
	public class Hilo extends Thread{
		private String asunto, cuerpo, dest;
		public Hilo(String dest, String asunto, String cuerpo) {
			this.dest = dest;
			this.asunto = asunto;
			this.cuerpo = cuerpo;
		}
		public void run() {
			try {
				Wallapop.sendMail(dest, asunto, cuerpo, "ingenieriaweb2021@gmail.com", "davigjwawfbsrkvk");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
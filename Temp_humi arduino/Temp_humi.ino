#include <Wire.h>  // Arduino IDE 內建
// LCD I2C Library，從這裡可以下載：
// https://bitbucket.org/fmalpartida/new-liquidcrystal/downloads
#include <LiquidCrystal_I2C.h>
#include <LiquidCrystal.h>
#include <DHT11.h>

    // 宣告溫濕度檢測器程式物件
const byte dataPin = 2;

DHT11 dht11(dataPin); 

LiquidCrystal_I2C lcd(0x27, 2, 1, 0, 4, 5, 6, 7, 3, POSITIVE);  // 設定 LCD I2C 位址

void setup() {
  Serial.begin(115200);
  lcd.begin(16, 2);
  
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Use Serial Mon");
  lcd.setCursor(0, 1);
  lcd.print("Type to display");

  lcd.begin(16, 2);       // 初始化 LCD
  lcd.setCursor(4, 0);
  lcd.print("Temp");
  lcd.setCursor(0, 1);
  lcd.print("Humidity");
  
}

void loop() {
  
  int err;
  float temp, humi;
  if((err=dht11.read(humi, temp))==0)
  {
    lcd.setCursor(9, 0);
    lcd.print(temp);
    lcd.print((char) 0xDF);
    lcd.print("C");
    lcd.setCursor(9, 1);   // 顯示濕度
    lcd.print(humi);
    lcd.print("%");
    Serial.println(temp);
    Serial.println(humi);
  }


  /*
  int chk = DHT11.read(dataPin);
  if (chk == 0) {
    lcd.setCursor(9, 0);   // 顯示溫度
    lcd.print((float)DHT11.temperature, 2);
    lcd.print((char) 0xDF);
    lcd.print("C");
 
    lcd.setCursor(9, 1);   // 顯示濕度
    lcd.print((float)DHT11.humidity, 2);
    lcd.print("%");
  }
  delay(2000);*/
}

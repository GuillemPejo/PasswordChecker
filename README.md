# 🔑 PasswordChecker


Android app that calculates password strength, verifies user entries, and gives recommendations on what the user should improve to get a stronger password.

It also implements an algorithm that calculates how long it would take a computer to crack the password and showed using LiveData


## How algorithm works

We calculate the number of possibilities for each set of password characters:
<br>
-> for A-Z is 26 ^ number_of_characters
<br>
-> for a-z is 26 ^ number_of_characters
<br>
-> for 0-9 is 10 ^ number_of_characters
<br>
-> for symbols is 32 ^ number_of_characters

As a result, it is divided by 2 to reduce keyspace search by Law of Averages and multiplied by the assigned workload average / computer (number of keys that a desktop computer can test efficiently in one hour (= 2 * 2 ^ 33)) and the estimated gross number of hours to crack the password is obtained.

Then just do a few simple calculations to get milliseconds / seconds / minutes / hours / days / weeks / months / years / centuries

<a href="https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&ved=2ahUKEwi1udiq0-HuAhXytHEKHQVmB0EQFjAAegQIARAC&url=http%3A%2F%2Fwww-scf.usc.edu%2F~csci530l%2Fdownloads%2FBFTCalc-modified.xls&usg=AOvVaw3nNHOgv1tjSeZ2ApsCcWUg">Learn more here</a>
<br>
<br>

<img src="/screenshots/Screenshot_1.png" alt="screenshot_main" width="300"/> <img src="/screenshots/Screenshot_2.png " alt="screenshot_detail" width="300"/> 
<img src="/screenshots/Screenshot_3.png " alt="screenshot_detail" width="300"/><img src="/screenshots/Screenshot_4.png " alt="screenshot_detail" width="300"/> 
<img src="/screenshots/Screenshot_5.png " alt="screenshot_detail" width="300"/> 

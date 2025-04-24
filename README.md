# gsc-reader
The **superior remake** of [Swag Studio](https://github.com/ViveTheModder/swag-studio) that still serves the same purpose: to **parse story mode files** (from Budokai Tenkaichi 2, Budokai Tenkaichi 3 and Raging Blast 1) and **extract information from them**.

Swag Studio had 29 (``LittleEndian.java``) + 848 (``MainApp.java``) + 66 (``MsgBox.java``) lines of code, which include empty lines (or lines with just ``{`` / ``}``), making up a total of **943 lines of code**.

All of that, only to support Budokai Tenkaichi 3 GSCs...

GSC Reader on the other hand has:
* 368 (``App.java``);
* 308 (``GSC.java``);
* 47 (``LittleEndian.java``);
* 126 (``Main.java``).
  
That is a total of **849 lines of code**, **94 lines less** than what Swag Studio had.

# Demonstration
This folder contains 10 **GSC files from BT3**. However, **2 of them have been modified** to be deemed **faulty** and therefore **skipped**.

![image](https://github.com/user-attachments/assets/11f94fbd-c243-47d6-aa49-c7a0c3e8ab96)

As for this folder, it contains the following files:
* ``10_GSC-DBC-003-000.gsc``, from Raging Blast 1;
* ``GSC_253_Cell_20B.gsc``, from Budokai Tenkaichi 2;
* ``GSC-B-00.gsc``, from Budokai Tenkaichi 3.

![image](https://github.com/user-attachments/assets/1fcaab5f-c1d5-4da0-abe9-7ba88425f2f5)

## CLI
![image](https://github.com/user-attachments/assets/6c9cd56e-78e5-4a94-ace0-660234d60ded)

![image](https://github.com/user-attachments/assets/8adad4f0-7096-498e-8153-5ab5470632c3)

## GUI
![image](https://github.com/user-attachments/assets/242e714c-9100-45da-9223-e58863405421)

![image](https://github.com/user-attachments/assets/d309cb6b-d021-4c9a-b5d1-1be921f5d0d7)

![image](https://github.com/user-attachments/assets/22c67650-c297-4378-9799-f13d6209f110)

![image](https://github.com/user-attachments/assets/544c20bd-dce0-4885-b65a-8c522ce8090a)

## Output
![image](https://github.com/user-attachments/assets/3f9f7620-5f35-4ef0-a87f-ba44bac59692)

![image](https://github.com/user-attachments/assets/f0f96704-40e9-442b-92da-b3367c1e24dc)

![image](https://github.com/user-attachments/assets/41621411-1328-4a27-9e2a-d9e2e34156df)

![image](https://github.com/user-attachments/assets/d5c5d499-6c1c-46b2-a762-053b78622473)

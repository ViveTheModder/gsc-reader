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
![image](https://github.com/user-attachments/assets/6c6a8ae0-e187-4e65-8da1-9153f1233cb6)

![image](https://github.com/user-attachments/assets/b5db7819-8a11-4271-8958-0dad29002791)

![image](https://github.com/user-attachments/assets/bc1eff17-f8ac-4b6b-97b3-856e27822df5)

![image](https://github.com/user-attachments/assets/1a668582-3e4e-4d1a-a499-a6d95e8e2503)

![image](https://github.com/user-attachments/assets/b83afa8c-07a3-4a74-808a-3444e5c2b898)

![image](https://github.com/user-attachments/assets/17d007be-8208-49e7-95b6-84978ea89312)

## Output
![image](https://github.com/user-attachments/assets/3f9f7620-5f35-4ef0-a87f-ba44bac59692)

![image](https://github.com/user-attachments/assets/f0f96704-40e9-442b-92da-b3367c1e24dc)

![image](https://github.com/user-attachments/assets/41621411-1328-4a27-9e2a-d9e2e34156df)

![image](https://github.com/user-attachments/assets/d5c5d499-6c1c-46b2-a762-053b78622473)

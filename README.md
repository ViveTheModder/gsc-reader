# gsc-reader
The **superior remake** of [Swag Studio](https://github.com/ViveTheModder/swag-studio) that still serves the same purpose: to **parse story mode files** (from Budokai Tenkaichi 2, Budokai Tenkaichi 3 and Raging Blast 1) and **extract information from them**.

Swag Studio had 29 (``LittleEndian.java``) + 848 (``MainApp.java``) + 66 (``MsgBox.java``) lines of code, which include empty lines (or lines with just ``{`` / ``}``), making up a total of **943 lines of code**.

All of that, only to support Budokai Tenkaichi 3 GSCs...

GSC Reader on the other hand has:
* 331 (``App.java``);
* 282 (``GSC.java``);
* 40 (``LittleEndian.java``);
* 144 (``Main.java``).
  
That is a total of **797 lines of code**, **146 lines less** than what Swag Studio had.

# Demonstration
This folder contains 10 **GSC files from BT3**. However, **2 of them have been modified** to be deemed **faulty** and therefore **skipped**.

![image](https://github.com/user-attachments/assets/11f94fbd-c243-47d6-aa49-c7a0c3e8ab96)

As for this folder, it contains the following files:
* ``10_GSC-DBC-003-000.gsc``, from Raging Blast 1;
* ``GSC_253_Cell_20B.gsc``, from Budokai Tenkaichi 2;
* ``GSC-B-00.gsc``, from Budokai Tenkaichi 3.

![image](https://github.com/user-attachments/assets/1fcaab5f-c1d5-4da0-abe9-7ba88425f2f5)

## CLI (Different GSC Samples)
<img width="1115" height="109" alt="image" src="https://github.com/user-attachments/assets/cc0cafca-812e-4a9b-a610-b64aec0521ed" />

<img width="747" height="52" alt="image" src="https://github.com/user-attachments/assets/75289eea-fc27-4073-bd6d-ed10b9f59635" />

<img width="1123" height="759" alt="image" src="https://github.com/user-attachments/assets/24fc9f8f-255e-4669-a300-bd5fb9f48c51" />

<img width="438" height="651" alt="image" src="https://github.com/user-attachments/assets/200a1568-e6bb-4859-beb5-98c7a4664667" />

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

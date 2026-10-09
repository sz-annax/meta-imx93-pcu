# IM356-M-A LCD Display Platform Bring-Up

This is an experimental board port sharing the PCU-IP image, not a production
BSP or a hardware-validated release. The first acceptance criterion is a stable U-Boot
console followed by Linux login from microSD. The optional hardware population
with an AUO P294IVN02.0 now has Linux LVDS support; backlight brightness defaults
to zero until the panel supply, cable and LVDS straps have been checked.

## Hardware Basis

References supplied for this port:

- IM356-M-A schematic revision A, 2026-06-11, 14 sheets.
- NXP IMX6SDLAEC revision 9, November 2018.
- NXP lf_v2024.04 U-Boot, commit e3219a5a73445219df605d1492687918d488055c.
- NXP lf-6.12.y Linux, commit 37d02f4dcbbe6677dc9f5fc17f386c05d6a7bd7a.

MCIMX6U6AVM08AC is **i.MX6DualLite**, not i.MX6UL or i.MX6ULL. It has two
Cortex-A9 cores. With the board's 24 MHz crystal, its 800 MHz speed grade means
792 MHz maximum. Linux OPPs are limited to 396/792 MHz.

| Function | Schematic connection | Initial support |
| --- | --- | --- |
| DDR3 | Four MT41K128M16JT, 64-bit, CS0, 1 GiB | Uncalibrated SABRE SD DCD candidate |
| Debug UART1 | SD3_DAT7 TX, SD3_DAT6 RX | 115200 8N1, no flow control |
| microSD | USDHC2, 4-bit, CD GPIO3_IO20 active low | 3.3 V, maximum 25 MHz |
| 16 GB eMMC | USDHC4, 8-bit | 3.3 V, maximum 25 MHz; no HS200/1.8 V |
| Ethernet | FEC RMII to IP101GR, MDIO address 1 | Linux driver, polling PHY |
| RMII clock | IP101 50 MHz output to GPIO_16 | External input, not SoC clock output |
| PHY reset | GPIO_17 = GPIO7_IO12 | Active low; also resets IP175 |
| LCD panel power | EIM_D28 = GPIO3_IO28 | DRM panel-controlled 12 V enable |
| Backlight enable | SD1_DAT0 = GPIO1_IO16 | PWM backlight, initially off |
| Backlight PWM | SD1_CMD / PWM4, transistor inversion | Inverted 1 kHz PWM |

The Ethernet topology is FEC -> RMII IP101 -> analog MDI -> IP175 port 0,
not a conventional direct MAC-to-switch CPU port. No DSA configuration or
GPIO switch-management bus is enabled. End-to-end LAN operation still depends
on IP175 strap/default behavior and board testing. No MAC address is invented;
verify the fused address or provision a unique address before network testing.

## DDR and Power: Before Trusting the Image

The ROM DCD currently comes from the official
`board/freescale/mx6sabresd/mx6dlsabresd.cfg`: 1 GiB, 64-bit DDR3, nominal
400 MHz DDR clock. Matching geometry does **not** validate its write-leveling,
DQS gating or read/write delay values for this PCB. DCD runs before U-Boot,
so bad DDR parameters can cause silence on UART even with correct UART pinmux.

Before production, use the NXP i.MX6 DDR initialization/calibration tools and
DDR stress test for the actual memory part, routing and operating conditions.
Replace the candidate DCD with measured board values and test cold/warm boots,
all memory lanes, full address coverage, temperature and supply corners.
Linux memtester is a useful smoke test, not a replacement for DDR calibration.

The schematic uses discrete supplies rather than the SABRE SD PFUZE100 PMIC.
This port does not access that PMIC or enable LDO bypass. Check ARM/SOC input
rails, DDR supply, internal regulator outputs and datasheet headroom before
testing. Schematic divider estimates are not voltage measurements.

## Build

The prepared directory is `../build_imx6u_lcd` relative to this layer.
From the BSP root, in a supported shell:

```sh
source setup-environment build_imx6u_lcd
bitbake pcu-ip-image
```

Configuration:

```bitbake
MACHINE = "imx6u-lcd"
DISTRO = "imx6u-lcd"
DL_DIR ?= "/opt/yocto/downloads/"
SSTATE_DIR = "${BSPDIR}/build_imx6ull/sstate-cache"
```

The new `conf/distro/imx6u-lcd.conf` inherits the common policy from
`imx93-pcu.conf` and overrides the distro identity. It reuses the unchanged
`recipes-pcu/images/pcu-ip-image.bb`, including its applications, systemd,
PipeWire and Go version policy. It also carries over the existing
`build_imx93` acceptance of `commercial` license flags required by libmad
and other multimedia components.

It uses `linux-imx-pcu` and `u-boot-imx-pcu`. Board sources are maintained in
their own Git repositories, not installed over upstream files by this layer.
The machine-scoped kernel append selects the source-owned config fragment.
The distro masks the base-files append containing another board's `fw_env.config`
and GoPoint/Chromium/ONNX appends whose layers are not enabled in this build.
Do not restore the old broad `recipes-(?!lcd/)` BBMASK: it hides
`pcu-ip-image` and causes `Nothing PROVIDES 'pcu-ip-image'`.

Before the local-source migration, `bitbake pcu-ip-image` completed successfully
with the USB/Fastboot changes.
The factory bundle passed UUU 1.5.165 offline parsing, WIC Zstandard integrity
checking, and comparison of the embedded and standalone U-Boot bytes.
These are build/artifact checks, not hardware validation. The shared PCU
applications may still require board-specific configuration. Private source
repositories require the same Git/SSH access as the existing PCU build.
For an isolated serial/storage test without the PCU application stack, the
optional `bitbake imx6u-lcd-image` target remains available.

Do not add the old `meta-imx6-pcu` or `build_imx6ull/workspace` layer to this
build. Do not reuse the old `tmp` directory. BitBake reuses compatible sstate
signatures; Cortex-A7 and Cortex-A9 target packages are not interchangeable.
Keep `conf/local.conf.sample`: the setup script uses it to recognize an
already initialized build directory. Build configuration files live outside
this layer's Git repository and must be retained separately.

### Local Source Development

The prepared build's `conf/local.conf` enables:

```bitbake
INHERIT += "externalsrc"
EXTERNALSRC:pn-u-boot-imx-pcu = "/home/annax/Documents/work/linux/u-boot-imx-pcu"
EXTERNALSRC:pn-linux-imx-pcu = "/home/annax/Documents/work/linux/linux-imx-pcu"
EXTERNALSRC_SYMLINKS:pn-u-boot-imx-pcu = ""
EXTERNALSRC_SYMLINKS:pn-linux-imx-pcu = ""
```

Uncommitted source changes participate in builds. Outputs remain in Yocto's
recipe build directories under `tmp/work`; do not build in the source tree.
Old in-tree products were cleared with `make mrproper` during migration,
with the user's approval. Do not repeat that command when retaining a custom
`.config`; save the configuration first. Do not run SDK and BitBake builds
concurrently against the same source tree.

U-Boot owns `board/annax/imx6u_lcd/`, `configs/imx6u_lcd_defconfig`,
`include/configs/imx6u-lcd.h`, and the board DTS/DTSI under `arch/arm/dts/`.
Its independent `TARGET_IMX6U_LCD` does not replace SABRE SD board code.
Linux owns the board DTS/DTSI under `arch/arm/boot/dts/nxp/imx/` and
`arch/arm/configs/imx6u-lcd.config`. Both device-tree Makefiles register the
new DTB. Edit each repository directly and commit its board files together
with its Kconfig/Makefile integration changes.

From a fresh shell at the BSP root:

```sh
source setup-environment build_imx6u_lcd
bitbake -c deploy u-boot-imx-pcu linux-imx-pcu
bitbake pcu-ip-image
```

Deploying the two components alone does not rebuild the WIC/rootfs or update
`imx6u-lcd-download`. Rebuild the image before creating a new factory bundle
so the kernel and rootfs modules match.

On 2026-09-18, `bitbake -c deploy u-boot-imx-pcu linux-imx-pcu` completed
successfully from both local trees (1032 tasks, 1014 reused). U-Boot, Linux
`zImage`, the board DTB, and kernel modules compiled successfully. The final
kernel configuration enables FEC, ICPLUS_PHY, MMC_SDHCI_ESDHC_IMX, and EXT4_FS
built-in. The user subsequently confirmed the migrated baseline works on the
board. The previous WIC/factory bundle was not regenerated by this component
build. New peripheral support below still requires separate board validation.

With externalsrc, the board append tracks both `imx_v7_defconfig` and
`imx6u-lcd.config` as inputs to `do_copy_defconfig`. Changing either reruns the
copy/merge/configure chain, not just compilation. Otherwise a successful build
can silently retain the old driver configuration.

For reproducible builds elsewhere, first commit and push both source repositories,
then pin the resulting `SRCREV:imx6u-lcd` values in machine-scoped recipe
appends and disable the local `EXTERNALSRC` settings. The shared recipes'
existing SRCREVs intentionally remain unchanged for other boards; those old
revisions do not contain this new board and cannot build it without externalsrc.
Do not use `AUTOREV` for a release.

### Existing i.MX6ULL SDK

The SDK's ARM cross-compiler can build this ARMv7 board. Its default application
flags target Cortex-A7, so use the plain compiler executable and let U-Boot or
Linux choose CPU flags. In a separate, fresh shell:

```sh
source /opt/imx6ull-pcu/6.12-styhead/environment-setup-cortexa7t2hf-neon-poky-linux-gnueabi
unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS KCFLAGS
export ARCH=arm CROSS_COMPILE=arm-poky-linux-gnueabi-
export CC=arm-poky-linux-gnueabi-gcc HOSTCC=gcc HOSTCXX=g++
uboot=/home/annax/Documents/work/linux/u-boot-imx-pcu
kernel=/home/annax/Documents/work/linux/linux-imx-pcu
uboot_out="$HOME/build/imx6u-lcd-sdk/u-boot"
kernel_out="$HOME/build/imx6u-lcd-sdk/linux"
make -C "$uboot" O="$uboot_out" imx6u_lcd_defconfig
make -C "$uboot" O="$uboot_out" -j5 u-boot.imx
make -C "$kernel" O="$kernel_out" imx_v7_defconfig
make -C "$kernel" O="$kernel_out" imx6u-lcd.config
make -C "$kernel" O="$kernel_out" -j5 zImage nxp/imx/imx6dl-imx6u-lcd.dtb
```

SDK validation during migration generated `u-boot.imx`, resolved the kernel
configuration fragment, and compiled the board DTB. A full SDK `zImage` build
was not part of that check. Use Yocto for the matching packaged kernel modules
and complete rootfs. These compile checks do not replace hardware tests or
validate the i.MX6ULL SDK's user-space libraries for a production Cortex-A9 image.

Outputs are under `build_imx6u_lcd/tmp/deploy/images/imx6u-lcd/`:

- `pcu-ip-image-imx6u-lcd.rootfs.wic.zst`: complete disk image.
- `pcu-ip-image-imx6u-lcd.rootfs.wic.bmap`: block map.
- `u-boot.imx`: ROM-loadable bootloader with candidate DDR DCD.
- `zImage` and `imx6dl-imx6u-lcd.dtb`: Linux kernel and board device tree.

Use the actual symlinks produced by the build if deployment naming changes.
The official WKS places U-Boot at 1 KiB and FAT partition 1 at 4 MiB, then ext4
partition 2 after the FAT partition (which may grow beyond its 16 MiB minimum).
Use `fdisk -l` on the uncompressed WIC for actual partition offsets. Write the complete WIC at disk
offset zero; do not add a separate seek offset to a WIC image.

## Factory eMMC Flashing Without TF

`recipes-lcd/uuu-emmc.uuu` implements ROM SDP -> RAM U-Boot -> USB Fastboot
-> eMMC. No TF card or existing operating system is required. The board port
now enables the ChipIdea USB gadget and NXP UUU Fastboot implementation on
USB OTG. ROM USB boot automatically enters `fastboot 0`; normal storage boot
retains the existing boot command.

**Destructive operation:** this script overwrites the eMMC user-area disk
image and boot0 bootloader, then changes EXT_CSD PARTITION_CONFIG to boot
from boot0 (boot ACK disabled, normal access returned to the user area).
It does not burn SoC fuses, configure RPMB, or permanently write-protect the
device. Back up existing data and qualify this process on a test board first.

Rebuild the full image so both the standalone and WIC-embedded U-Boot contain
the new USB support. Do not reuse the earlier 275 KiB RAM-only U-Boot:

```sh
source setup-environment build_imx6u_lcd
bitbake pcu-ip-image
```

If the existing U-Boot already enters UUU Fastboot, the September 17
compressed-image fix only requires the corrected script and compressed WIC;
no U-Boot or rootfs rebuild is needed.

From this layer's root, prepare the download files in a fresh release directory:

```sh
mkdir -p ../imx6u-lcd-download
cp recipes-lcd/uuu-emmc.uuu ../imx6u-lcd-download/
cp -L ../build_imx6u_lcd/tmp/deploy/images/imx6u-lcd/u-boot.imx ../imx6u-lcd-download/
cp -L ../build_imx6u_lcd/tmp/deploy/images/imx6u-lcd/pcu-ip-image-imx6u-lcd.rootfs.wic.zst ../imx6u-lcd-download/
```

Use UUU 1.5.165 or newer on the factory PC; the same tool supports i.MX91/93.
The three required files in one directory are `uuu-emmc.uuu`, `u-boot.imx`
and **`pcu-ip-image-imx6u-lcd.rootfs.wic.zst`**. No uncompressed WIC, standalone
kernel, DTB, rootfs tarball, bmap or separate Windows zstd executable is needed.
Copy real file contents (`cp -L`), not deployment symlinks. Keep a release
checksum manifest and use bootloader/WIC from the same build. UUU decompresses
on the host and sends raw-to-sparse chunks within the 256 MiB Fastboot buffer
at 0x20000000; the whole WIC need not fit in board RAM.

Do not pass a plain `.wic.zst` path to `flash -raw2sparse`: in UUU 1.5.165
the ordinary-file loader reads that path as compressed bytes, not a disk
image. The earlier script made this mistake. Use the explicit UUU compressed
stream path, including the trailing `/*` (keep the forward slash on Windows):

```text
FB: flash -raw2sparse all pcu-ip-image-imx6u-lcd.rootfs.wic.zst/*
```

This is UUU virtual-path syntax, not a real directory or shell wildcard.
The script passes it directly to UUU. A read-only probe linked to the built
UUU 1.5.165 library compared every byte loaded through this path against the
raw WIC: all 1,431,128,064 bytes matched. Offline `-dry` also passes, but
`-dry` alone checks syntax/file availability, not decompression correctness.
The Windows executable and actual board write still require a test run.

1. Connect UART1 at 115200 8N1 and the board's USB OTG data port. Verify power,
   VBUS wiring and the external watchdog bypass before beginning.
2. Select BOOT_MODE 01 (ROM serial downloader), then power-cycle/reset.
   Use one connected test board for initial qualification.
3. From the download directory, run the explicit destructive script:

```sh
uuu -lsusb
uuu -v uuu-emmc.uuu
```

On Windows PowerShell use `.\uuu.exe -v uuu-emmc.uuu`. Install the host's
UUU USB access rules/drivers as necessary. On Linux, use sudo only if the
host's USB permissions require it.

The script checks `uuu_board=imx6u-lcd`, explicitly sets the NXP backend's
`target_ubootdev=3`, and checks that MMC 3 boot0 is accessible before writing.
USDHC4/eMMC is MMC 3 by device-tree alias, not MMC 2 as on SABRE SD.
`flash all` writes the WIC at offset zero of the user area. `flash bootloader`
writes `u-boot.imx` at 1 KiB in boot0, as required by this i.MX6 backend.
After the user-area write, the script rescans MMC 3, reads partition 2's UUID,
and loads partition 1's `zImage` and board DTB. Only after these checks and the
bootloader write succeed does `mmc partconf 3 0 1 0` select boot0.
Any failing UCmd/flash command stops the sequence. The identity check prevents
accidental use with an unrelated U-Boot; it is not cryptographic authentication.

Wait for UUU success with zero failures. The script does not reset the board:
switch to BOOT_MODE 10 with verified **USDHC4/eMMC** boot straps, then
power-cycle and check Linux login without a TF card. BOOT_MODE alone does not
select USDHC4; do not follow inherited SD3 labels or burn fuses to guess it.
Disconnecting power during a write can leave an unbootable device; re-enter
ROM download and repeat the complete script after power is stable.

If an earlier flash boots U-Boot but reports `No partition table - mmc 3`,
read sector zero without writing anything:

```text
mmc dev 3 0
mmc read ${loadaddr} 0 1
md.b ${loadaddr} 10
```

`28 b5 2f fd` at the beginning identifies a Zstandard stream mistakenly
written as a disk image. The correct WIC contains an MBR with signature
`55 aa` at byte offsets 510/511. Re-enter ROM USB mode and rerun the corrected
script using the `.wic.zst/*` stream; do not attempt to repair it by creating a
partition table on top of the compressed data.

This is a factory-workflow candidate, not a production-qualified flasher.
The September 17 board log confirms USB download and boot0 U-Boot startup.
DDR DCD remains uncalibrated. Full readback verification, Linux cold boot from
eMMC, watchdog handling and application operation must still be validated
on real hardware before batch use. The script's read checks do not perform
full-image readback or prove successful Linux boot. This debug U-Boot permits
arbitrary UUU commands and is not a secure-boot provisioning solution.

## UUU RAM-Only Download

The same NXP UUU executable used for i.MX91/i.MX93 supports i.MX6DualLite,
but their flashing scripts and bootloader images are not interchangeable.
This board uses the ROM SDP protocol, not the newer SoCs' SDPS sequence.
The supplied `recipes-lcd/uuu-ramboot.uuu` loads `u-boot.imx` into RAM and
starts it. It does not write TF/eMMC, download Linux/rootfs, or change fuses.
The current U-Boot then enters Fastboot automatically. For whole-eMMC flashing,
use the board-specific script above, not a generic `uuu -b emmc_all` script
that assumes another board's eMMC environment settings.

Prepare a download directory from this layer's root:

```sh
mkdir -p ../imx6u-lcd-download
cp recipes-lcd/uuu-ramboot.uuu ../imx6u-lcd-download/
cp -L ../build_imx6u_lcd/tmp/deploy/images/imx6u-lcd/u-boot.imx ../imx6u-lcd-download/
cp -L ../build_imx6u_lcd/tmp/deploy/images/imx6u-lcd/pcu-ip-image-imx6u-lcd.rootfs.wic.zst ../imx6u-lcd-download/
cp -L ../build_imx6u_lcd/tmp/deploy/images/imx6u-lcd/pcu-ip-image-imx6u-lcd.rootfs.wic.bmap ../imx6u-lcd-download/
```

`cp -L` copies the real artifacts rather than broken deployment symlinks.
Use artifacts from the same completed build. Copy this directory to the
download PC and use its existing UUU executable:

| File | Purpose |
| --- | --- |
| `uuu-ramboot.uuu` | Required UUU RAM boot script |
| `u-boot.imx` | Required ROM-loadable bootloader, including DDR DCD |
| `pcu-ip-image-imx6u-lcd.rootfs.wic.zst` | Complete TF disk image; write using a card reader, not this UUU script |
| `pcu-ip-image-imx6u-lcd.rootfs.wic.bmap` | Optional block map for bmaptool; not used by UUU or dd |

The WIC already contains U-Boot, `zImage`, the board DTB and rootfs.
There is no need to copy the standalone kernel, DTB, rootfs tar archive,
modules archive, environment file or `u-boot.imx.tagged` for this workflow.

Set BOOT_MODE to ROM serial download (01), connect the board's USB OTG
port with a data cable, open UART1 at 115200 8N1, and reset/power-cycle the
board. From the download directory, on Linux:

```sh
uuu -lsusb
uuu uuu-ramboot.uuu
```

Use the host's UUU udev rules, or run with sudo if USB permissions require
it. On Windows PowerShell, with `uuu.exe` in the download directory:

```powershell
.\uuu.exe -lsusb
.\uuu.exe uuu-ramboot.uuu
```

After RAM-only ROM download, check the serial console. Exit Fastboot with
Ctrl-C on that console to inspect U-Boot. `bootcmd` is temporarily `fastboot 0`
for this USB session; power-cycle into correctly strapped storage boot to use
the normal TF-first/eMMC-second boot command.
RAM download does not survive power loss and does not change boot straps.
If UUU detects the ROM but no U-Boot console appears, check DDR DCD, power,
watchdog and UART wiring. The DDR values remain uncalibrated and USB RAM
boot has not yet been verified on this board.

## First Boot

1. Confirm power rails, DDR assembly and the hardware watchdog bypass/debug
   path. The M41T94 watchdog shares WDI with RUN LED GPIO5_IO28 and can drive
   the power-cycle circuit through TS5A3159. This image does not service it.
   Do not assume a missing driver disables an already-running watchdog.
2. Connect a correctly leveled UART adapter to LMC J19: pin 15 is CPU TX,
   pin 16 is CPU RX; verify ground against the connector drawing. Cross TX/RX.
   Do not connect an RS232-voltage adapter or feed its supply into the board.
3. Back up any existing card data. On the host, inspect `lsblk` and unmount
   all partitions of the selected removable card. The command below destroys
   the selected card's previous contents. Replace `/dev/REPLACE_WITH_SD_CARD`
   with the verified **whole-card** device, not a partition or host disk.

```sh
zstd -d -k pcu-ip-image-imx6u-lcd.rootfs.wic.zst
sudo dd if=pcu-ip-image-imx6u-lcd.rootfs.wic of=/dev/REPLACE_WITH_SD_CARD bs=4M conv=fsync status=progress
```

4. Select internal boot with the correct USDHC2 SD straps, if the board
   permits it. BOOT_MODE 10 alone does not select the storage controller.
   The schematic contains inherited SD3 boot labels although the actual
   eMMC is on USDHC4. Verify populated pull resistors and the reference
   manual before changing straps. Do not burn fuses during bring-up.
5. If straps select eMMC and cannot be changed, use BOOT_MODE 01 (ROM serial
   download), connect the LMC USB OTG port, and use NXP UUU on a host to load
   `u-boot.imx` into RAM. Run the supplied script from the download directory:

```sh
uuu uuu-ramboot.uuu
```

This uses the SoC ROM USB protocol and then enters Fastboot. USB mass storage
and TFTP remain disabled. Use the board-specific factory script for eMMC writes.
ROM download still requires a valid DDR DCD and has not been tested on this
board. The schematic's long-button-press downloader circuit must be verified.

6. Interrupt normal storage autoboot, or exit Fastboot with Ctrl-C after a
   RAM-only download, and check:

```text
bdinfo
mmc list
mmc dev 1 0
mmc info
ls mmc 1:1
mmc dev 3 0
mmc info
```

Expected aliases: MMC 1 = USDHC2/TF; MMC 3 = USDHC4/eMMC. USDHC3 stays off
because its DAT6/7 pins carry the console. RAM should be 1 GiB at 0x10000000.
Use explicit, verified unused ranges for `mtest`; never run it over U-Boot,
its stack, loaded kernel or device tree. Do not use its unqualified defaults.

7. On normal storage boot, `run bootcmd` tries TF first, then the eMMC user area. Both use partition 1
   for `zImage`/DTB and partition 2 for rootfs. Root is selected by PARTUUID,
   not a hard-coded Linux MMC device name. There is no persistent U-Boot
   environment and no automatic eMMC write.
8. Log in as `root` on the serial console using the shared PCU image's
   configured password. This debug image intentionally
   enables Yocto `debug-tweaks`; do not deploy it on an untrusted network.

```sh
cat /proc/cpuinfo
cat /proc/meminfo
dmesg | grep -Ei 'mmc|fec|phy|error|fail'
lsblk
cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies
memtester 128M 1
ip link
```

Memory reported by Linux will be less than 1 GiB because of reservations.
After stable small tests, increase memtester allocation while leaving enough
RAM for the OS. For Ethernet, verify a unique MAC, inspect `ethtool eth0`,
then configure an address appropriate for an isolated test network.

TF testing is optional with the factory USB flow, but validate DDR before
trusting eMMC writes. Back up eMMC first. Whole-device WIC writes target the
eMMC **user area**, not boot0; the factory script separately writes the boot0
bootloader and selects it via EXT_CSD. Boot straps must still select USDHC4.

## Peripheral Bring-Up

The following additions are in the local Linux repository's board DTS and
`arch/arm/configs/imx6u-lcd.config`, not layer-owned copies. SDK DTB compilation
and `bitbake -c deploy linux-imx-pcu` passed (911 tasks, 894 reused on the final
run). Every requested built-in option was checked against the actual Yocto
`.config`; kernel, DTB and modules compiled and deployed. DT schema validation
was not run because its tools were unavailable. No new peripheral has yet been
verified electrically or exercised on this board. Rebuild `pcu-ip-image` before
flashing a complete image; the existing WIC and factory bundle are unchanged.

| Peripheral | Connection | Enabled interface |
| --- | --- | --- |
| PCF85263 RTC | I2C1, CSI0_DAT9/8, address 0x51 | `rtc0`; SoC SNVS becomes `rtc1` |
| ADS1015 ADC | I2C2, KEY_COL3/ROW3, address 0x48 | IIO, four single-ended inputs |
| LM75B temperature | I2C2, address 0x49 | hwmon temperature, millidegrees C |
| Expansion I2C | I2C3 GPIO_3/6; I2C4 NANDF_WP_B/CS3 | 100 kHz, no invented child devices |
| SLM9670 TPM | ECSPI1 KEY_COL0/ROW0/COL1; CS1 GPIO4_IO10 | TIS SPI at 1 MHz, polling |
| M41T94 watchdog | ECSPI3 CS0 GPIO5_IO18; WDI GPIO5_IO28 | Existing watchdog driver, mode 3, 60 s default |
| W25Q32JV NOR | ECSPI3 CS1 GPIO4_IO25 | 4 MiB MTD, 10 MHz, no partitions |
| FM25L16B FRAM | ECSPI3 CS2 GPIO5_IO19 | 2048-byte AT25/NVMEM, 1 MHz |
| Factory-reset button | DISP0_DAT9 / GPIO4_IO30, active low | `KEY_RESTART`, 30 ms debounce |
| ERR LED | EIM_D23 / GPIO3_IO23, active low | `imx6u-lcd:red:error`, initially off |
| IRM3638 IR receiver | EIM_CS0 / GPIO2_IO23, active low | LIRC, NEC/RC5/RC6 decoding |
| USB host | USBH1, J20, fixed 5 V supply | USB HID and HID multitouch drivers |
| USB OTG | LMC USB port | Peripheral mode; configfs ACM/ECM/RNDIS/mass-storage support |

All I2C buses start at 100 kHz. ADS1015 DT channel addresses 4/5/6/7 select
AIN0/1/2/3; sysfs uses `in_voltage0_raw` through `in_voltage3_raw`. The selected
PGA range is +/-4.096 V at 1600 samples/s, but the 3.3 V supply still limits
allowable pin voltage: do not apply 4.096 V to an ADC pin. Scale is in mV/LSB;
multiply raw by scale for pin voltage, then apply the actual board divider
ratio. No board-level physical-unit conversion is assumed.

TPM uses the existing SLB9670-compatible TIS binding with `tcg,tpm_tis-spi`
fallback. GPIO1_IO02 is held high to release reset; IRQ GPIO1_IO04 is unused.
Confirm SLM9670 identification on hardware. ECSPI3 data pads are DISP0_DAT0/1/2
for SCLK/MOSI/MISO. USB gadget capabilities alone do not create a gadget or
export storage; no gadget or watchdog service is added. Touch requires actual
USB enumeration to confirm that the fitted controller uses HID.

Board-ID bits 0..7 map to GPIO6_IO04, GPIO6_IO02, GPIO6_IO00, GPIO5_IO30,
GPIO6_IO05, GPIO6_IO03, GPIO6_IO01, GPIO5_IO31. Their pads are muxed as GPIO
with weak internal pulldowns, not claimed as output GPIOs. Request them as
inputs with libgpiod after identifying each bank via `gpioinfo`; do not assume
global gpiochip numbering. Unpopulated strap options can read low from the
internal bias and do not constitute a valid programmed board ID.

### Read-Only Board Checks

Boot the matching new kernel and DTB, then start with:

```sh
uname -a
dmesg | grep -Ei 'pcf85|ads1015|lm75|spi|tpm|m41t94|watchdog|gpio-ir|usb|error|fail'
cat /sys/class/rtc/rtc*/name
hwclock -r -f /dev/rtc0
cat /sys/class/hwmon/hwmon*/name
cat /sys/class/hwmon/hwmon*/temp1_input
cat /sys/bus/iio/devices/iio:device*/name
cat /sys/bus/iio/devices/iio:device*/in_voltage0_raw
cat /sys/bus/iio/devices/iio:device*/in_voltage0_scale
ls -l /sys/bus/spi/devices/*/driver
cat /proc/mtd
ls /sys/bus/nvmem/devices
ls /sys/class/tpm
cat /proc/bus/input/devices
cat /sys/class/rc/rc*/protocols
cat /sys/class/leds/imx6u-lcd:red:error/brightness
ls /sys/bus/usb/devices
ls /sys/class/udc
cat /sys/class/watchdog/watchdog*/identity
cat /sys/class/watchdog/watchdog*/state
```

Select the device with the matching name if multiple sensors are present.
An unset RTC may report invalid time until intentionally initialized.
If installed, `evtest` can observe the button, `ir-keytable -t` can observe IR
scancodes, and `lsusb` can identify touch hardware. No remote keymap is guessed.
The button node only generates an input event; no factory-erase handler is
added. FRAM can be inspected via the matching NVMEM device's `nvmem` file;
confirm its device/size first. Do not run flash erase/write, TPM provisioning,
FRAM writes, or broad I2C probing as initial presence checks.

### Watchdog Constraints

M41T94 registration does not call its start operation and no userspace feeding
service is added. Opening its `/dev/watchdogN` starts it, so use the sysfs
checks above instead of opening that device for inspection. Probe does not
disable a watchdog inherited from firmware. The existing driver initializes
the oscillator during probe; this is not a passive presence test.

The existing rootfs has runtime watchdog disabled but `RebootWatchdogSec=60`.
Systemd may therefore use its selected watchdog during shutdown/reboot;
registration order does not guarantee that this is the external device.
This change does not alter that existing systemd policy. For a bench test
requiring no systemd watchdog use at all, explicitly set `RuntimeWatchdogSec=0`,
`RebootWatchdogSec=0` and `KExecWatchdogSec=0` in a system.conf.d drop-in before
testing, and separately verify firmware/hardware watchdog state.

RUN LED shares WDI and is deliberately not exposed as a separate LED. Its
state follows the watchdog GPIO. The LMC DBG_IO0-controlled TS5A3159 reset gate
is not driven by this DTS; verify its bypass/power-cycle behavior externally.
Do not assume driver registration guarantees that expiry can reset the board.
The existing shared M41T94 driver's `set_timeout` only updates cached state,
not an already-running hardware timer. Do not rely on changing the timeout
after opening the device until that driver behavior is corrected and tested.

## AUO P294IVN02.0 LVDS Display

The Linux board DTS selects this panel population. Do not deploy it to a board
with a different panel or supply without adapting the configuration. Enabling
the DRM output can turn on panel power even with backlight brightness zero.
U-Boot is unchanged and still holds panel power and backlight off.

The input is **1920x1080 at 60 Hz**, although the physical visible area is
**1920x720**. The AUO Rev 1.0 timing table and the reference GXA function 0x307
use 1080-line transport. The reference profile reports a visible rectangle of
`(0, 0, 1920, 720)`. Keep application content inside that rectangle and confirm
its origin with a test pattern on the actual panel. This patch does not resize
or crop application/compositor content. The datasheet's conflicting copied
timing note should be clarified with the supplier before production.

| Parameter | Configuration |
| --- | --- |
| Full pixel clock | 148.5 MHz |
| Horizontal active/front/sync/back | 1920 / 88 / 44 / 148 |
| Vertical active/front/sync/back | 1080 / 4 / 5 / 36 |
| Totals | 2200 x 1125 |
| LVDS | Dual link, RGB888, VESA/SPWG, DE high |
| Per-link clock | 74.25 MHz |
| Sync polarity | Negative H, positive V; panel uses DE |
| Power enable | GPIO3_IO28 high, 30 ms regulator startup allowance |
| Panel delays | 40 ms prepare, 640 ms enable, 20 ms disable, 1000 ms off |
| Backlight | GPIO1_IO16 high, PWM4 inverted at 1 kHz, default brightness 0 |

The panel's LVDS_SEL must be high/open for VESA. A JEIDA-strapped assembly
needs `data-mapping = "jeida-24"` on the panel node instead. Verify the two
links against the schematic and connector: panel CH1 carries odd-numbered
pixels, CH2 even-numbered pixels. Do not copy the GXA GM8775C "swapped" setting:
that is specific to the bridge and its board wiring. This board uses native
LDB; no GM8775C, CS5518, RK628 or GXA daemon is added. Confirm the measured
panel rail is 12 V within the datasheet limits; a regulator voltage in DTS
describes the hardware and does not set a fixed regulator's output voltage.

Implementation is source-owned:

- `drivers/gpu/drm/panel/panel-simple.c`: `auo,p294ivn02-0` mode and delays.
- `Documentation/devicetree/bindings/display/panel/panel-simple.yaml`: binding.
- `arch/arm/boot/dts/nxp/imx/imx6dl-imx6u-lcd.dts`: regulator, backlight,
  panel graph, DRM LDB clocks and dual-channel output.
- `arch/arm/configs/imx6u-lcd.config`: DRM IPUv3/LDB/panel/PWM support.

LDB parents are selected in the CCM node for the i.MX6 glitch-safe early
clock-switch sequence. The existing external 50 MHz Ethernet reference is
still selected, now on the FEC node rather than in the CCM assignment list.

This machine uses DRM/KMS instead of NXP's legacy MXC framebuffer/IPU driver.
The fragment disables the legacy IPU and LDB drivers to prevent two drivers
claiming the same hardware. Existing GPU properties and other machines are
unchanged. DRM framebuffer emulation is enabled, but applications relying on
NXP-specific framebuffer ioctls or vendor EGL integrations need separate
compatibility checks. The Weston and Chromium integration below is separate
from this kernel configuration.

The existing machine-specific Yocto append merges this fragment and tracks it
for externalsrc builds. Rebuild from the BSP root:

```sh
source setup-environment build_imx6u_lcd
bitbake -c deploy linux-imx-pcu
bitbake pcu-ip-image
```

Use the kernel and DTB from the same build. Deploying the kernel alone does not
refresh the disk image or factory bundle. For a first board check, inspect:

```sh
dmesg | grep -Ei 'drm|ipu|ldb|panel|backlight|pwm|regulator'
ls /sys/class/drm
cat /sys/class/drm/card*-LVDS-*/modes
cat /sys/class/backlight/backlight-lvds/max_brightness
cat /sys/class/backlight/backlight-lvds/brightness
```

Expect a 1920x1080 mode and a maximum brightness index of 10. After validating
power sequencing and LVDS signals, enable a low brightness as root:

```sh
echo 2 > /sys/class/backlight/backlight-lvds/brightness
```

Use a DRM test pattern (for example `modetest` from libdrm's test package when
installed) to check RGB channels, alternating odd/even columns and the visible
720-line rectangle. Verify panel VDD rise time (0.4-30 ms), valid data at least
40 ms after VDD reaches 90%, at least 640 ms before backlight, and at least
1000 ms power-off before restart with an oscilloscope. Also test display
blank/unblank, suspend/resume and warm reboot. Build checks are not hardware
qualification and do not validate the physical PWM inversion or LVDS wiring.

## Chromium on the LCD

The LCD image includes `chromium-lcd`, which depends on
`chromium-ozone-wayland` and supplies a non-root systemd launcher. The prepared
`build_imx6u_lcd/conf/bblayers.conf` now includes
`${BSPDIR}/sources/meta-browser/meta-chromium`; retain this entry when
recreating the build directory. The local multimedia Chromium append remains
masked: it enables proprietary video codecs for a different board.

This uses Chromium 129.0.6668.100 from the existing layer and the NXP dynamic
layer's i.MX6 Vivante patches. The generated launcher uses Wayland and native
EGL (`--ozone-platform=wayland --use-gl=egl`). NXP disables GBM on i.MX6 and
removes bundled ANGLE EGL/GLES libraries so that the system Vivante libraries
are used. Keep the NXP GPU workarounds, including disabled MSAA and GPU
rasterization; GPU compositing and GPU rasterization are different features.
VA-API, V4L2 hardware codecs and proprietary codecs are not enabled here.

A version-specific LCD patch fixes Chromium 129's media GPU sandbox GN
configuration: without hardware codecs, it must not require Mesa's `dri.pc`
from the Vivante EGL stack. This does not disable the browser sandbox.

Weston GL rendering has been confirmed on the board with
`GL renderer: Vivante GC880`. That does not establish browser acceleration.
Chromium patch application and GN configuration, plus the launcher install
task, have passed; a full Chromium/image build and browser board test are
still required.

Build and deploy the root filesystem:

```sh
source setup-environment build_imx6u_lcd
bitbake imx6u-lcd-image
```

Chromium is a large host build even for simple target pages. The host checked
during integration had about 148 GiB free disk and 94 GiB RAM. Monitor disk
usage and memory during compilation; keep the prepared build's five-job
parallelism initially. This integration does not require kernel/DTB changes.

The service is deliberately disabled until board acceptance. On the board,
set the URL in `/etc/default/chromium-lcd` to `chrome://gpu` for validation or
to the application's URL, then run:

```sh
systemctl start chromium-lcd.service
systemctl status chromium-lcd.service --no-pager
journalctl -b -u chromium-lcd.service --no-pager
```

It runs as the existing `weston` user, with a private runtime directory,
profile in `/var/lib/chromium-lcd`, and access to Weston's `/run/wayland-0`
socket through the `wayland` group. Do not start Chromium as root or add
`--no-sandbox`. The kernel configuration supports namespaces and seccomp;
the installed Chromium setuid sandbox and device permissions also need to
be retained by deployment.

In `chrome://gpu`, require hardware-accelerated compositing and a Vivante
renderer, not SwiftShader or llvmpipe. GPU rasterization may remain disabled
by the NXP workaround; video decoding is outside this acceptance test. Check
scrolling, CSS animations and page reloads on the actual lightweight
application, and observe CPU/RAM usage over repeated navigation. The
renderer-process limit is a soft process limit, not a memory cap. Once the
application passes, enable the launcher at boot:

```sh
systemctl enable chromium-lcd.service
```

Chromium 129 is an old BSP integration baseline, not a current secure browser
release. Restrict this initial deployment to trusted controlled content;
before production or arbitrary Internet access, update the browser and
revalidate the NXP patches. Avoid unnecessary tabs, extensions and large
JavaScript workloads on the dual-core, 1 GiB target.

## Remaining Hardware Questions

- Confirm the populated panel is P294IVN02.0, its 12 V supply, LVDS_SEL strap,
  odd/even link wiring, visible rectangle and inverted PWM against hardware.
- Confirm calibrated DDR values and populated boot-configuration resistors.
- Verify switch MDC physically at KEY_ROW1/GPIO4_IO09, despite the net label
  claiming GPIO4_IO31; resolve switch MDIO/GPIO1_IO08 versus CAN1_RX labeling.
- GPIO_17 is GPIO7_IO12, not GPIO1_IO17. SD3 debug GPIO net labels also have
  bank mismatches; use physical pad names and official pinmux headers.
- Confirm the external watchdog's debug/bypass behavior. RUN LED heartbeat
  must not be added independently of the shared WDI function.
- The reused CPU symbol includes Quad-only SATA and VDD_CACHE_CAP labels.
  Compare the populated board against the DualLite NC-pin table before
  treating these connections as valid hardware.
- LCD support requires the display checks above; audio remains disabled pending
   codec details. USB touch is enabled only to the extent supported by USB HID.
- UART2 remains disabled until its two alternative schematic pin routes are
   resolved. IP175 management remains disabled pending the MDC/MDIO clarification.
- Confirm RTC crystal load, installed ID straps, peripheral identities and
   watchdog reset-gate wiring against the populated board before qualification.

# IM356-M-A LCD Display Platform Bring-Up

This is an experimental board port sharing the PCU-IP image, not a production
BSP or a hardware-validated release. The first acceptance criterion is a stable U-Boot
console followed by Linux login from microSD. LCD output is intentionally off.

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
| LCD panel power | EIM_D28 = GPIO3_IO28 | Held low |
| Backlight enable | SD1_DAT0 = GPIO1_IO16 | Held low |
| Backlight PWM | SD1_CMD / PWM4, transistor inversion | Disabled |

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

It uses official `linux-imx` and `u-boot-imx`, with machine-scoped appends
under `recipes-lcd`. The distro masks only the old PCU bootloader/kernel
recipes, the base-files append containing another board's `fw_env.config`,
and GoPoint/Chromium/ONNX appends whose layers are not enabled in this build.
Do not restore the old broad `recipes-(?!lcd/)` BBMASK: it hides
`pcu-ip-image` and causes `Nothing PROVIDES 'pcu-ip-image'`.

`bitbake pcu-ip-image` completed successfully with the USB/Fastboot changes.
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

## Remaining Hardware Questions

- Supply the panel model, voltage, resolution/timings, LVDS single/dual-link,
  JEIDA/VESA mapping and backlight polarity before enabling display power.
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
- RTC, I2C sensors, SPI NOR/FRAM/TPM/watchdog, audio, USB touch and LCD are
  deferred until the serial/storage baseline is verified on the board.

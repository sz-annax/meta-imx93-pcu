#include <env.h>
#include <init.h>
#include <asm/arch/imx-regs.h>
#include <asm/arch/mx6-pins.h>
#include <asm/arch/sys_proto.h>
#include <asm/global_data.h>
#include <asm/mach-imx/iomux-v3.h>
#include <asm/mach-imx/sys_proto.h>
#include <power/regulator.h>

DECLARE_GLOBAL_DATA_PTR;

#define UART_PAD_CTRL (PAD_CTL_PUS_100K_UP | PAD_CTL_SPEED_MED | \
		      PAD_CTL_DSE_40ohm | PAD_CTL_SRE_FAST | PAD_CTL_HYS)

static iomux_v3_cfg_t const uart1_pads[] = {
	IOMUX_PADS(PAD_SD3_DAT7__UART1_TX_DATA | MUX_PAD_CTRL(UART_PAD_CTRL)),
	IOMUX_PADS(PAD_SD3_DAT6__UART1_RX_DATA | MUX_PAD_CTRL(UART_PAD_CTRL)),
};

int dram_init(void)
{
	gd->ram_size = imx_ddr_size();
	return 0;
}

int board_early_init_f(void)
{
	SETUP_IOMUX_PADS(uart1_pads);
	return 0;
}

int board_init(void)
{
	gd->bd->bi_boot_params = PHYS_SDRAM + 0x100;
	return regulators_enable_boot_on(false);
}

int board_late_init(void)
{
	puts("IM356-M-A: experimental DDR settings; validate with NXP DDR stress test.\n");
	env_set("uuu_board", "imx6u-lcd");
	env_set("fastboot_dev", "mmc3");
	env_set("target_ubootdev", "3");
	env_set("mmcdev", "3");
	if (is_boot_from_usb()) {
		puts("ROM USB boot: entering UUU Fastboot on USB OTG.\n");
		env_set("bootcmd", "fastboot 0");
		env_set("bootdelay", "0");
	}
	return 0;
}

int checkboard(void)
{
	puts("Board: IM356-M-A LCD Display Platform (i.MX6DualLite)\n");
	return 0;
}
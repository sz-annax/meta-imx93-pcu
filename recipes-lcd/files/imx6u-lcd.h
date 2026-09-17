#ifndef __IMX6U_LCD_CONFIG_H
#define __IMX6U_LCD_CONFIG_H

#include "mx6_common.h"

#define CFG_MXC_UART_BASE UART1_BASE
#define PHYS_SDRAM MMDC0_ARB_BASE_ADDR
#define PHYS_SDRAM_SIZE (1024u * 1024 * 1024)
#define CFG_SYS_SDRAM_BASE PHYS_SDRAM
#define CFG_SYS_INIT_RAM_ADDR IRAM_BASE_ADDR
#define CFG_SYS_INIT_RAM_SIZE IRAM_SIZE
#define CFG_SYS_FSL_ESDHC_ADDR 0
#define CFG_SYS_FSL_USDHC_NUM 2

#define CFG_EXTRA_ENV_SETTINGS \
	"fdt_addr_r=0x18000000\0" \
	"fdt_high=0xffffffff\0" \
	"stdin=serial\0" \
	"stdout=serial\0" \
	"stderr=serial\0"

#endif
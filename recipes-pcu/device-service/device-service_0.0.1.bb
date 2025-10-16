DESCRIPTION = "Device Management Service for i.MX PCU"
LICENSE = "CLOSED"

GO_IMPORT = "github.com/sz-annax/device-service"

SRC_URI = "git://git@${GO_IMPORT}.git;protocol=ssh;destsuffix=${BPN}-${PV}/src/${GO_IMPORT};branch=main \
           file://device-config.yaml \
           file://device.service \
          "
SRCREV = "5e0024152d71530ef102709ad3a5f2e1ea86b73a"

# Upstream repo does not tag
UPSTREAM_CHECK_COMMITS = "1"

export GOPROXY = "https://proxy.golang.org,direct"
# Workaround for network access issue during compile step.
# This needs to be fixed in the recipes buildsystem so that
# it can be accomplished during do_fetch task.
do_compile[network] = "1"

B = "${S}/src/${GO_IMPORT}/bin"

# Version and build information
GO_EXTRA_LDFLAGS += ' -X ${GO_IMPORT}/conf.VERSION=${PV} -X ${GO_IMPORT}/conf.BUILD=${SRCREV}'

CGO_ENABLED = "1"
GO_INSTALL = "."

inherit go-mod systemd

# Prevent stripping binaries, which causes debugging issues
INSANE_SKIP:${PN} += "already-stripped"
INSANE_SKIP:${PN} += "textrel"
INSANE_SKIP:${PN}-dev += "textrel"

do_install:append() {
    rm -f ${D}/usr/lib/go/src/${GO_IMPORT}/build-arm64.sh

    # /etc/pcu/device-config.yaml
    install -d ${D}${sysconfdir}/pcu
    install -m 0644 ${UNPACKDIR}/device-config.yaml ${D}${sysconfdir}/pcu

    # /usb/lib/systemd/system/device.service
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/device.service ${D}${systemd_unitdir}/system
}

SYSTEMD_SERVICE:${PN} = "device.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

SUMMARY = "U-Boot fw_env.config file"
LICENSE = "CLOSED"

SRC_URI = "file://fw_env.config file://my-echo-cancel.conf"

S = "${UNPACKDIR}"

do_install() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${UNPACKDIR}/fw_env.config ${D}${sysconfdir}

    install -d ${D}${sysconfdir}/pipewire/pipewire.conf.d
    install -m 0644 ${UNPACKDIR}/my-echo-cancel.conf ${D}${sysconfdir}/pipewire/pipewire.conf.d
}

# FILES:${PN} = "${sysconfdir}/fw_env.config ${sysconfdir}/pipewire/pipewire.conf.d/my-echo-cancel.conf"
FILES:${PN} += "${sysconfdir}"

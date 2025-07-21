SUMMARY = "PCU Application Configuration Files"
LICENSE = "CLOSED"

SRC_URI = "file://fw_env.config file://my-echo-cancel.conf file://pcu_data.db"

S = "${UNPACKDIR}"

do_install() {
    # fw_env.config
    install -d ${D}${sysconfdir}
    install -m 0644 ${UNPACKDIR}/fw_env.config ${D}${sysconfdir}

    # pcu_data.db
    install -d ${D}${sysconfdir}/pcu
    install -m 0644 ${UNPACKDIR}/pcu_data.db ${D}${sysconfdir}/pcu

    # my-echo-cancel.conf
    install -d ${D}${sysconfdir}/pipewire/pipewire.conf.d
    install -m 0644 ${UNPACKDIR}/my-echo-cancel.conf ${D}${sysconfdir}/pipewire/pipewire.conf.d
}

# FILES:${PN} = "${sysconfdir}/fw_env.config ${sysconfdir}/pipewire/pipewire.conf.d/my-echo-cancel.conf"
FILES:${PN} += "${sysconfdir}"

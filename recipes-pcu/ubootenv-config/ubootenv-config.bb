SUMMARY = "U-Boot fw_env.config file"
LICENSE = "CLOSED"

SRC_URI = "file://fw_env.config"

S = "${UNPACKDIR}"

do_install() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${UNPACKDIR}/fw_env.config ${D}${sysconfdir}
}

FILES:${PN} = "${sysconfdir}/fw_env.config"

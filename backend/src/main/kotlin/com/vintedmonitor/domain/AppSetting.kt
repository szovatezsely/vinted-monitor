package com.vintedmonitor.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "app_settings")
class AppSetting(
    @Id
    @Column(name = "setting_key", length = 64)
    var key: String = "",

    @Column(name = "setting_value", length = 1024)
    var value: String? = null
)

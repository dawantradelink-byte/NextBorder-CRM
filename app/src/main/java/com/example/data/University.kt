package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "universities")
data class University(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val contactName: String,
    val email: String,
    val acceptsAgents: Boolean = false,
    val moiAccepted: Boolean = false,
    val bbaAvailable: Boolean = false,
    val mbaAvailable: Boolean = false,
    val tuitionFees: String = "",
    val scholarshipsAvailable: Boolean = false,
    val casDepositPolicy: String = "",
    val installmentOptions: Boolean = false,
    val bdFriendly: Boolean = false,
    val intlPercentage: String = "",
    val status: String = "New", // New, Contacted, FollowUp1, FollowUp2, Meeting, Partnered, Archived
    val website: String = "",
    val country: String = "United Kingdom",
    val city: String = "",
    val intakeMonths: String = "Jan, Sep",
    val commissionRate: String = "15%",
    val applicationFee: String = "Free",
    val priority: String = "Medium", // High, Medium, Low
    val partnershipStatus: String = "ACTIVE", // ACTIVE, CONTACTED, FOLLOW_UP, MEETING, PARTNER, COMPLETED, NO_RESPONSE, GREYLIST, BLACKLIST
    val lastContactedDate: Long = 0L,
    val nextFollowUpDate: Long = 0L,
    val notes: String = "",
    val tags: String = "Middle-Class, Easy Admission",
    val assignedCounselor: String = "Ishak Dewan",
    val whatsappNumber: String = "+447700900077",
    val isArchived: Boolean = false,
    val greylistReason: String = "",
    val greylistRefusalDate: Long = 0L,
    val greylistConversation: String = "",
    val greylistConfidenceScore: Double = 0.0,
    val greylistSuggestedFollowUpDate: Long = 0L,
    val greylistRecommendedStrategy: String = "",
    val greylistAiAnalysis: String = "",
    val blacklistReason: String = "",
    val blacklistDate: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

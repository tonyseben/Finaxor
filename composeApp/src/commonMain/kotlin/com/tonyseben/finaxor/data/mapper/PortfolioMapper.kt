package com.tonyseben.finaxor.data.mapper

import com.tonyseben.finaxor.data.entity.PortfolioEntity
import com.tonyseben.finaxor.data.entity.PortfolioMemberEntity
import com.tonyseben.finaxor.domain.model.Portfolio
import com.tonyseben.finaxor.domain.model.PortfolioMember
import com.tonyseben.finaxor.domain.model.PortfolioRole
import kotlin.jvm.JvmName

fun PortfolioEntity.toDomain(): Portfolio {
    return Portfolio(
        id = id,
        name = name,
        createdBy = createdBy,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Portfolio.toEntity(): PortfolioEntity {
    return PortfolioEntity(
        id = id,
        name = name,
        createdBy = createdBy,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun PortfolioMemberEntity.toDomain(): PortfolioMember {
    return PortfolioMember(
        userId = userId,
        portfolioId = portfolioId,
        role = role.toPortfolioRole() ?: PortfolioRole.VIEWER,
        addedBy = addedBy,
        addedAt = addedAt
    )
}

fun PortfolioMember.toEntity(): PortfolioMemberEntity {
    return PortfolioMemberEntity(
        userId = userId,
        portfolioId = portfolioId,
        role = role.toRoleString(),
        addedBy = addedBy,
        addedAt = addedAt
    )
}

fun String.toPortfolioRole(): PortfolioRole? {
    return when (this.lowercase()) {
        "owner" -> PortfolioRole.OWNER
        "member" -> PortfolioRole.MEMBER
        "viewer" -> PortfolioRole.VIEWER
        else -> null
    }
}

fun PortfolioRole.toRoleString(): String {
    return when (this) {
        PortfolioRole.OWNER -> "owner"
        PortfolioRole.MEMBER -> "member"
        PortfolioRole.VIEWER -> "viewer"
    }
}


@JvmName("portfoliosToDomain")
fun List<PortfolioEntity>.toDomain(): List<Portfolio> = map { it.toDomain() }

@JvmName("membersToDomain")
fun List<PortfolioMemberEntity>.toDomain(): List<PortfolioMember> = map { it.toDomain() }
package com.splitwise.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "`group`")
public class Group {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "group_id")
	private int groupId;

	@Column(name = "group_name")
	private String groupName;

	@Column(name = "simplify_debt")
	private boolean simplifyDebt;

	@ManyToOne
	@JoinColumn(name = "created_by")
	private User createdBy;

	@Column(name = "deleted")
	private boolean deleted = false;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
	private List<GroupMember> members;
}

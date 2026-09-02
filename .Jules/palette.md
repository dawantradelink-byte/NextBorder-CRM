## 2023-10-27 - Icon Accessibility
**Learning:** In Jetpack Compose, IconButton requires a contentDescription for screen readers. Several instances were set to null, hiding them from accessibility services.
**Action:** Always provide meaningful content descriptions for interactive Icon components.

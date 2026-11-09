import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(const WeChatApp());
}

/// ============================================================================
/// 1. BRAND COLORS & THEME SPECIFICATIONS
/// Official brand colors: Primary Orange (#E15712), Secondary Blue (#2FA0E1)
/// ============================================================================

class WeChatColors {
  static const Color primaryOrange = Color(0xFFE15712);
  static const Color primaryOrangeDark = Color(0xFFB8420B);
  static const Color primaryOrangeLight = Color(0xFFFF7E3D);

  static const Color secondaryBlue = Color(0xFF2FA0E1);
  static const Color secondaryBlueDark = Color(0xFF1A7BB8);
  static const Color secondaryBlueLight = Color(0xFF70C1F5);

  // Dark Theme Palette
  static const Color darkBackground = Color(0xFF111418);
  static const Color darkSurface = Color(0xFF1A1E24);
  static const Color darkSurfaceVariant = Color(0xFF242A33);
  static const Color darkTextPrimary = Color(0xFFF1F3F5);
  static const Color darkTextSecondary = Color(0xFFA0AAB8);
  static const Color darkBorder = Color(0xFF2E3540);

  // Light Theme Palette
  static const Color lightBackground = Color(0xFFF7F9FB);
  static const Color lightSurface = Color(0xFFFFFFFF);
  static const Color lightSurfaceVariant = Color(0xFFEEF2F6);
  static const Color lightTextPrimary = Color(0xFF181C20);
  static const Color lightTextSecondary = Color(0xFF5A6675);
  static const Color lightBorder = Color(0xFFCFD8DC);
}

class WeChatTheme {
  static ThemeData get lightTheme {
    return ThemeData(
      useMaterial3: true,
      brightness: Brightness.light,
      primaryColor: WeChatColors.primaryOrange,
      scaffoldBackgroundColor: WeChatColors.lightBackground,
      colorScheme: const ColorScheme.light(
        primary: WeChatColors.primaryOrange,
        onPrimary: Colors.white,
        secondary: WeChatColors.secondaryBlue,
        onSecondary: Colors.white,
        surface: WeChatColors.lightSurface,
        onSurface: WeChatColors.lightTextPrimary,
        surfaceContainerHighest: WeChatColors.lightSurfaceVariant,
        outline: WeChatColors.lightBorder,
      ),
      appBarTheme: const AppBarTheme(
        backgroundColor: WeChatColors.lightSurface,
        foregroundColor: WeChatColors.lightTextPrimary,
        elevation: 0,
        centerTitle: false,
        scrolledUnderElevation: 1,
      ),
      navigationBarTheme: NavigationBarThemeData(
        backgroundColor: WeChatColors.lightSurface,
        indicatorColor: WeChatColors.primaryOrange.withOpacity(0.15),
        iconTheme: WidgetStateProperty.resolveWith((states) {
          if (states.contains(WidgetState.selected)) {
            return const IconThemeData(color: WeChatColors.primaryOrange);
          }
          return const IconThemeData(color: WeChatColors.lightTextSecondary);
        }),
        labelTextStyle: WidgetStateProperty.resolveWith((states) {
          if (states.contains(WidgetState.selected)) {
            return const TextStyle(
              fontSize: 12,
              fontWeight: FontWeight.bold,
              color: WeChatColors.primaryOrange,
            );
          }
          return const TextStyle(
            fontSize: 12,
            fontWeight: FontWeight.w500,
            color: WeChatColors.lightTextSecondary,
          );
        }),
      ),
      cardTheme: CardTheme(
        color: WeChatColors.lightSurface,
        elevation: 1,
        shape: RoundedCornerShape(16),
        margin: EdgeInsets.zero,
      ),
    );
  }

  static ThemeData get darkTheme {
    return ThemeData(
      useMaterial3: true,
      brightness: Brightness.dark,
      primaryColor: WeChatColors.primaryOrange,
      scaffoldBackgroundColor: WeChatColors.darkBackground,
      colorScheme: const ColorScheme.dark(
        primary: WeChatColors.primaryOrange,
        onPrimary: Colors.white,
        secondary: WeChatColors.secondaryBlue,
        onSecondary: Colors.white,
        surface: WeChatColors.darkSurface,
        onSurface: WeChatColors.darkTextPrimary,
        surfaceContainerHighest: WeChatColors.darkSurfaceVariant,
        outline: WeChatColors.darkBorder,
      ),
      appBarTheme: const AppBarTheme(
        backgroundColor: WeChatColors.darkSurface,
        foregroundColor: WeChatColors.darkTextPrimary,
        elevation: 0,
        centerTitle: false,
        scrolledUnderElevation: 1,
      ),
      navigationBarTheme: NavigationBarThemeData(
        backgroundColor: WeChatColors.darkSurface,
        indicatorColor: WeChatColors.primaryOrange.withOpacity(0.2),
        iconTheme: WidgetStateProperty.resolveWith((states) {
          if (states.contains(WidgetState.selected)) {
            return const IconThemeData(color: WeChatColors.primaryOrange);
          }
          return const IconThemeData(color: WeChatColors.darkTextSecondary);
        }),
        labelTextStyle: WidgetStateProperty.resolveWith((states) {
          if (states.contains(WidgetState.selected)) {
            return const TextStyle(
              fontSize: 12,
              fontWeight: FontWeight.bold,
              color: WeChatColors.primaryOrange,
            );
          }
          return const TextStyle(
            fontSize: 12,
            fontWeight: FontWeight.w500,
            color: WeChatColors.darkTextSecondary,
          );
        }),
      ),
      cardTheme: CardTheme(
        color: WeChatColors.darkSurface,
        elevation: 1,
        shape: RoundedCornerShape(16),
        margin: EdgeInsets.zero,
      ),
    );
  }

  static RoundedRectangleBorder RoundedCornerShape(double radius) {
    return RoundedRectangleBorder(borderRadius: BorderRadius.circular(radius));
  }
}

/// ============================================================================
/// 2. MAIN APPLICATION WIDGET
/// ============================================================================

class WeChatApp extends StatefulWidget {
  const WeChatApp({super.key});

  @override
  State<WeChatApp> createState() => _WeChatAppState();
}

class _WeChatAppState extends State<WeChatApp> {
  bool _isAuthenticated = false;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'WeChat',
      debugShowCheckedModeBanner: false,
      theme: WeChatTheme.lightTheme,
      darkTheme: WeChatTheme.darkTheme,
      themeMode: ThemeMode.system,
      home: _isAuthenticated
          ? MainNavigationScaffold(onLogout: () => setState(() => _isAuthenticated = false))
          : FlutterAuthScreen(onAuthenticated: () => setState(() => _isAuthenticated = true)),
    );
  }
}

/// ============================================================================
/// 2.1 AUTHENTICATION & ACCOUNT CREATION SCREEN
/// Features dedicated "Complete Account Creation" button
/// ============================================================================

class FlutterAuthScreen extends StatefulWidget {
  final VoidCallback onAuthenticated;

  const FlutterAuthScreen({super.key, required this.onAuthenticated});

  @override
  State<FlutterAuthScreen> createState() => _FlutterAuthScreenState();
}

class _FlutterAuthScreenState extends State<FlutterAuthScreen> {
  bool _isRegisterMode = true;

  final TextEditingController _fullNameController = TextEditingController();
  final TextEditingController _usernameController = TextEditingController();
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _phoneController = TextEditingController();
  final TextEditingController _dobController = TextEditingController();
  final TextEditingController _bioController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _confirmPasswordController = TextEditingController();

  String? _errorMessage;
  bool _isLoading = false;

  void _submitRegistration() {
    final fullName = _fullNameController.text.trim();
    final username = _usernameController.text.trim();
    final email = _emailController.text.trim();
    final password = _passwordController.text;
    final confirmPassword = _confirmPasswordController.text;

    if (fullName.isEmpty) {
      setState(() => _errorMessage = 'Please enter your full name.');
      return;
    }
    if (username.isEmpty || username.length < 3) {
      setState(() => _errorMessage = 'Username must be at least 3 characters.');
      return;
    }
    if (email.isEmpty || !email.contains('@')) {
      setState(() => _errorMessage = 'Please enter a valid email address.');
      return;
    }
    if (password.length < 6) {
      setState(() => _errorMessage = 'Password must be at least 6 characters.');
      return;
    }
    if (password != confirmPassword) {
      setState(() => _errorMessage = 'Passwords do not match.');
      return;
    }

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    // Simulate account registration & JWT session saving
    TokenManager().saveTokens(
      access: 'access_token_demo_${DateTime.now().millisecondsSinceEpoch}',
      refresh: 'refresh_token_demo_${DateTime.now().millisecondsSinceEpoch}',
    );

    Future.delayed(const Duration(milliseconds: 600), () {
      if (mounted) {
        setState(() => _isLoading = false);
        widget.onAuthenticated();
      }
    });
  }

  void _submitLogin() {
    final email = _emailController.text.trim();
    final password = _passwordController.text;

    if (email.isEmpty || password.isEmpty) {
      setState(() => _errorMessage = 'Please enter both email/username and password.');
      return;
    }

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    Future.delayed(const Duration(milliseconds: 600), () {
      if (mounted) {
        setState(() => _isLoading = false);
        widget.onAuthenticated();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Column(
          children: [
            Expanded(
              child: ListView(
                padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 20),
                children: [
                  // WeChat Brand Header
                  Center(
                    child: Container(
                      padding: const EdgeInsets.all(14),
                      decoration: BoxDecoration(
                        gradient: const LinearGradient(
                          colors: [WeChatColors.primaryOrange, WeChatColors.secondaryBlue],
                        ),
                        borderRadius: BorderRadius.circular(18),
                      ),
                      child: const Icon(Icons.chat_bubble_rounded, color: Colors.white, size: 38),
                    ),
                  ),
                  const SizedBox(height: 14),
                  const Center(
                    child: Text(
                      'WeChat',
                      style: TextStyle(fontWeight: FontWeight.bold, fontSize: 26),
                    ),
                  ),
                  const SizedBox(height: 4),
                  Center(
                    child: Text(
                      _isRegisterMode
                          ? 'Fill in your details and tap Complete Account Creation.'
                          : 'Sign in to access your network and conversations.',
                      style: const TextStyle(fontSize: 13, color: Colors.grey),
                      textAlign: TextAlign.Center,
                    ),
                  ),
                  const SizedBox(height: 20),

                  // Segmented Switcher (Sign In vs Create Account)
                  Container(
                    decoration: BoxDecoration(
                      color: Theme.of(context).colorScheme.surfaceContainerHighest,
                      borderRadius: BorderRadius.circular(12),
                    ),
                    padding: const EdgeInsets.all(4),
                    child: Row(
                      children: [
                        Expanded(
                          child: GestureDetector(
                            onTap: () => setState(() {
                              _isRegisterMode = false;
                              _errorMessage = null;
                            }),
                            child: Container(
                              padding: const EdgeInsets.symmetric(vertical: 10),
                              decoration: BoxDecoration(
                                color: !_isRegisterMode ? WeChatColors.primaryOrange : Colors.transparent,
                                borderRadius: BorderRadius.circular(10),
                              ),
                              child: Center(
                                child: Text(
                                  'Sign In',
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    color: !_isRegisterMode ? Colors.white : Colors.grey,
                                  ),
                                ),
                              ),
                            ),
                          ),
                        ),
                        Expanded(
                          child: GestureDetector(
                            onTap: () => setState(() {
                              _isRegisterMode = true;
                              _errorMessage = null;
                            }),
                            child: Container(
                              padding: const EdgeInsets.symmetric(vertical: 10),
                              decoration: BoxDecoration(
                                color: _isRegisterMode ? WeChatColors.primaryOrange : Colors.transparent,
                                borderRadius: BorderRadius.circular(10),
                              ),
                              child: Center(
                                child: Text(
                                  'Create Account',
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    color: _isRegisterMode ? Colors.white : Colors.grey,
                                  ),
                                ),
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 18),

                  if (_errorMessage != null) ...[
                    Container(
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: Colors.red.withOpacity(0.12),
                        borderRadius: BorderRadius.circular(10),
                        border: Border.all(color: Colors.red.withOpacity(0.3)),
                      ),
                      child: Row(
                        children: [
                          const Icon(Icons.error_outline, color: Colors.red, size: 20),
                          const SizedBox(width: 8),
                          Expanded(
                            child: Text(
                              _errorMessage!,
                              style: const TextStyle(color: Colors.red, fontSize: 13),
                            ),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: 14),
                  ],

                  if (_isRegisterMode) ...[
                    _buildTextField(_fullNameController, 'Full Name *', Icons.badge_outlined),
                    const SizedBox(height: 12),
                    _buildTextField(_usernameController, 'Unique Username (@handle) *', Icons.alternate_email_rounded),
                    const SizedBox(height: 12),
                    _buildTextField(_emailController, 'Email Address *', Icons.email_outlined, keyboardType: TextInputType.emailAddress),
                    const SizedBox(height: 12),
                    _buildTextField(_phoneController, 'Phone Number with Country Code', Icons.phone_outlined, keyboardType: TextInputType.phone),
                    const SizedBox(height: 12),
                    _buildTextField(_dobController, 'Date of Birth (YYYY-MM-DD)', Icons.calendar_today_outlined),
                    const SizedBox(height: 12),
                    _buildTextField(_bioController, 'Personal Biography / About', Icons.info_outline_rounded),
                    const SizedBox(height: 12),
                    _buildTextField(_passwordController, 'Password (min 6 chars) *', Icons.lock_outline_rounded, isPassword: true),
                    const SizedBox(height: 12),
                    _buildTextField(_confirmPasswordController, 'Confirm Password *', Icons.lock_reset_rounded, isPassword: true),
                    const SizedBox(height: 24),
                  ] else ...[
                    _buildTextField(_emailController, 'Username or Email', Icons.person_outline_rounded),
                    const SizedBox(height: 14),
                    _buildTextField(_passwordController, 'Password', Icons.lock_outline_rounded, isPassword: true),
                    const SizedBox(height: 24),
                  ],
                ],
              ),
            ),

            // Persistent Bottom Bar with Complete Account Creation Button
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
              decoration: BoxDecoration(
                color: Theme.of(context).colorScheme.surface,
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.06),
                    blurRadius: 10,
                    offset: const Offset(0, -3),
                  ),
                ],
              ),
              child: SizedBox(
                width: double.infinity,
                height: 52,
                child: ElevatedButton.icon(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: WeChatColors.primaryOrange,
                    foregroundColor: Colors.white,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
                  ),
                  icon: Icon(_isRegisterMode ? Icons.check_circle_rounded : Icons.login_rounded),
                  label: Text(
                    _isLoading
                        ? 'Processing...'
                        : (_isRegisterMode ? 'Complete Account Creation' : 'Sign In to WeChat'),
                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                  ),
                  onPressed: _isLoading
                      ? null
                      : (_isRegisterMode ? _submitRegistration : _submitLogin),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTextField(
    TextEditingController controller,
    String label,
    IconData icon, {
    bool isPassword = false,
    TextInputType keyboardType = TextInputType.text,
  }) {
    return TextField(
      controller: controller,
      obscureText: isPassword,
      keyboardType: keyboardType,
      decoration: InputDecoration(
        labelText: label,
        prefixIcon: Icon(icon, color: WeChatColors.primaryOrange, size: 20),
        border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
        contentPadding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
      ),
    );
  }
}

/// ============================================================================
/// 3. CORE NAVIGATION SCAFFOLD (5 MAIN SECTIONS)
/// Home, Discover, Create, Messages, Profile
/// ============================================================================

class MainNavigationScaffold extends StatefulWidget {
  final VoidCallback? onLogout;

  const MainNavigationScaffold({super.key, this.onLogout});

  @override
  State<MainNavigationScaffold> createState() => _MainNavigationScaffoldState();
}

class _MainNavigationScaffoldState extends State<MainNavigationScaffold> {
  int _currentIndex = 0;

  final List<Widget> _sections = const [
    MessagesSection(),
    HomeSection(),
    DiscoverSection(),
    CallsSection(),
    ProfileSection(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: IndexedStack(
        index: _currentIndex,
        children: _sections,
      ),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex,
        onDestinationSelected: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
        destinations: const [
          NavigationDestination(
            icon: Badge(
              label: Text('3'),
              backgroundColor: WeChatColors.primaryOrange,
              child: Icon(Icons.chat_bubble_outline_rounded),
            ),
            selectedIcon: Badge(
              label: Text('3'),
              backgroundColor: WeChatColors.primaryOrange,
              child: Icon(Icons.chat_bubble_rounded),
            ),
            label: 'Chats',
          ),
          NavigationDestination(
            icon: Icon(Icons.donut_large_rounded),
            selectedIcon: Icon(Icons.donut_large_rounded),
            label: 'Updates',
          ),
          NavigationDestination(
            icon: Icon(Icons.groups_outlined),
            selectedIcon: Icon(Icons.groups_rounded),
            label: 'Communities',
          ),
          NavigationDestination(
            icon: Icon(Icons.call_outlined),
            selectedIcon: Icon(Icons.call_rounded),
            label: 'Calls',
          ),
          NavigationDestination(
            icon: Icon(Icons.dashboard_customize_outlined),
            selectedIcon: Icon(Icons.dashboard_customize_rounded),
            label: 'Dashboard',
          ),
        ],
      ),
    );
  }
}

/// ============================================================================
/// 3.1 CALLS SECTION (WHATSAPP CALL LOGS & ENCRYPTED CALLING)
/// ============================================================================

class CallsSection extends StatelessWidget {
  const CallsSection({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Calls', style: TextStyle(fontWeight: FontWeight.bold)),
        actions: [
          IconButton(
            icon: const Icon(Icons.add_call, color: WeChatColors.primaryOrange),
            onPressed: () {},
          ),
        ],
      ),
      body: ListView(
        children: [
          ListTile(
            leading: CircleAvatar(
              backgroundColor: WeChatColors.primaryOrange.withOpacity(0.15),
              child: const Icon(Icons.link_rounded, color: WeChatColors.primaryOrange),
            ),
            title: const Text('Create a call link', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
            subtitle: const Text('Share an encrypted audio or video link for your WeChat call', style: TextStyle(fontSize: 12)),
            onTap: () {},
          ),
          const Divider(),
          const Padding(
            padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: Text('Recent', style: TextStyle(fontWeight: FontWeight.bold, color: Colors.grey, fontSize: 13)),
          ),
          _buildCallItem('Alex Chen', 'Today, 14:20', isVideo: true, isIncoming: true),
          const Divider(height: 1, indent: 72),
          _buildCallItem('Sophia Vance', 'Yesterday, 18:05', isVideo: false, isIncoming: false),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        backgroundColor: WeChatColors.primaryOrange,
        foregroundColor: Colors.white,
        child: const Icon(Icons.add_call),
        onPressed: () {},
      ),
    );
  }

  Widget _buildCallItem(String name, String time, {required bool isVideo, required bool isIncoming}) {
    return ListTile(
      leading: CircleAvatar(
        backgroundColor: WeChatColors.secondaryBlue.withOpacity(0.15),
        child: Text(name.substring(0, 1), style: const TextStyle(color: WeChatColors.secondaryBlue, fontWeight: FontWeight.bold)),
      ),
      title: Text(name, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
      subtitle: Row(
        children: [
          Icon(
            isIncoming ? Icons.call_received_rounded : Icons.call_made_rounded,
            size: 14,
            color: const Color(0xFF00C853),
          ),
          const SizedBox(width: 4),
          Text(time, style: const TextStyle(fontSize: 12, color: Colors.grey)),
        ],
      ),
      trailing: Icon(
        isVideo ? Icons.videocam_rounded : Icons.call_rounded,
        color: WeChatColors.primaryOrange,
      ),
    );
  }
}

/// ============================================================================
/// 4. SECTION 1: HOME (TIMELINE & STORIES)
/// ============================================================================

class HomeSection extends StatelessWidget {
  const HomeSection({super.key});

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      appBar: AppBar(
        title: Row(
          children: [
            Container(
              padding: const EdgeInsets.all(7),
              decoration: BoxDecoration(
                color: WeChatColors.primaryOrange,
                borderRadius: BorderRadius.circular(10),
              ),
              child: const Icon(Icons.chat_bubble_rounded, color: Colors.white, size: 18),
            ),
            const SizedBox(width: 10),
            const Text(
              'WeChat',
              style: TextStyle(fontWeight: FontWeight.bold, fontSize: 20),
            ),
          ],
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications_none_rounded),
            onPressed: () {},
          ),
          IconButton(
            icon: const Icon(Icons.search_rounded),
            onPressed: () {},
          ),
        ],
      ),
      body: ListView(
        padding: const EdgeInsets.symmetric(vertical: 12),
        children: [
          // Stories Carousel Bar
          SizedBox(
            height: 98,
            child: ListView(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.symmetric(horizontal: 16),
              children: [
                _buildAddStoryItem(context),
                const SizedBox(width: 12),
                _buildStoryItem(context, 'Alex Chen', WeChatColors.primaryOrange),
                const SizedBox(width: 12),
                _buildStoryItem(context, 'Sophia V.', WeChatColors.secondaryBlue),
                const SizedBox(width: 12),
                _buildStoryItem(context, 'Marcus T.', WeChatColors.primaryOrange),
              ],
            ),
          ),
          const Divider(height: 24, thickness: 0.5),

          // Feed Header Tabs
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
            child: Row(
              children: [
                _buildFilterChip(context, 'For You', isSelected: true),
                const SizedBox(width: 8),
                _buildFilterChip(context, 'Following'),
                const SizedBox(width: 8),
                _buildFilterChip(context, 'Trending'),
              ],
            ),
          ),
          const SizedBox(height: 12),

          // Welcome Card
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Card(
              color: isDark ? WeChatColors.darkSurface : WeChatColors.lightSurface,
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        CircleAvatar(
                          backgroundColor: WeChatColors.primaryOrange,
                          child: const Text('W', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                        ),
                        const SizedBox(width: 12),
                        const Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text('WeChat Platform Core', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                              Text('@wechat.official • Cryptographically Verified', style: TextStyle(fontSize: 12, color: WeChatColors.secondaryBlue)),
                            ],
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 12),
                    const Text(
                      'Welcome to the Next-Generation WeChat social communication platform. Connect securely, communicate in real time, and share within interactive communities.',
                      style: TextStyle(fontSize: 14, height: 1.4),
                    ),
                    const SizedBox(height: 12),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        _buildPostAction(Icons.favorite_border_rounded, 'Like'),
                        _buildPostAction(Icons.chat_bubble_outline_rounded, 'Comment'),
                        _buildPostAction(Icons.repeat_rounded, 'Repost'),
                        _buildPostAction(Icons.bookmark_border_rounded, 'Save'),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildAddStoryItem(BuildContext context) {
    return Column(
      children: [
        Container(
          width: 62,
          height: 62,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            border: Border.all(color: WeChatColors.primaryOrange, width: 2),
          ),
          child: const Center(
            child: Icon(Icons.add, color: WeChatColors.primaryOrange, size: 28),
          ),
        ),
        const SizedBox(height: 6),
        const Text('Your Story', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w500)),
      ],
    );
  }

  Widget _buildStoryItem(BuildContext context, String name, Color borderAccent) {
    return Column(
      children: [
        Container(
          width: 62,
          height: 62,
          padding: const EdgeInsets.all(2.5),
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            gradient: LinearGradient(
              colors: [WeChatColors.primaryOrange, WeChatColors.secondaryBlue],
            ),
          ),
          child: CircleAvatar(
            backgroundColor: borderAccent.withOpacity(0.2),
            child: Text(name.substring(0, 1), style: TextStyle(fontWeight: FontWeight.bold, color: borderAccent)),
          ),
        ),
        const SizedBox(height: 6),
        Text(name, style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w500)),
      ],
    );
  }

  Widget _buildFilterChip(BuildContext context, String label, {bool isSelected = false}) {
    return FilterChip(
      selected: isSelected,
      label: Text(label, style: TextStyle(color: isSelected ? Colors.white : null, fontSize: 12)),
      selectedColor: WeChatColors.primaryOrange,
      onSelected: (_) {},
    );
  }

  Widget _buildPostAction(IconData icon, String label) {
    return Row(
      children: [
        Icon(icon, size: 18, color: Colors.grey),
        const SizedBox(width: 4),
        Text(label, style: const TextStyle(fontSize: 12, color: Colors.grey)),
      ],
    );
  }
}

/// ============================================================================
/// 5. SECTION 2: DISCOVER (COMMUNITIES & LOCAL NETWORK)
/// ============================================================================

class DiscoverSection extends StatelessWidget {
  const DiscoverSection({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Discover', style: TextStyle(fontWeight: FontWeight.bold)),
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          // Search Input
          TextField(
            decoration: InputDecoration(
              hintText: 'Search communities, users, topics...',
              prefixIcon: const Icon(Icons.search, color: WeChatColors.primaryOrange),
              filled: true,
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
                borderSide: BorderSide.none,
              ),
              contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
            ),
          ),
          const SizedBox(height: 20),

          // Local Community Network Banner
          Card(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(16),
              side: const BorderSide(color: WeChatColors.secondaryBlue, width: 1),
            ),
            child: const Padding(
              padding: EdgeInsets.all(16),
              child: Row(
                children: [
                  CircleAvatar(
                    backgroundColor: WeChatColors.secondaryBlue,
                    child: Icon(Icons.near_me_rounded, color: Colors.white),
                  ),
                  SizedBox(width: 14),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text('Local Community Network', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                        SizedBox(height: 2),
                        Text('Explore local roundtables, campus groups, and tech events.', style: TextStyle(fontSize: 12, color: Colors.grey)),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 24),

          // Interactive Communities Header
          const Text('Interactive Communities', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
          const SizedBox(height: 12),
          _buildCommunityCard('Modern Software Architects', 'Technology • 1,480 members', WeChatColors.secondaryBlue),
          const SizedBox(height: 10),
          _buildCommunityCard('UI/UX & Product Design', 'Creative • 920 members', WeChatColors.primaryOrange),
          const SizedBox(height: 10),
          _buildCommunityCard('Global Founders & Creators', 'Business • 610 members', WeChatColors.secondaryBlueDark),
        ],
      ),
    );
  }

  Widget _buildCommunityCard(String name, String meta, Color accent) {
    return Card(
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: accent.withOpacity(0.15),
          child: Icon(Icons.groups_rounded, color: accent),
        ),
        title: Text(name, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
        subtitle: Text(meta, style: const TextStyle(fontSize: 12)),
        trailing: ElevatedButton(
          style: ElevatedButton.styleFrom(
            backgroundColor: WeChatColors.primaryOrange,
            foregroundColor: Colors.white,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 4),
          ),
          onPressed: () {},
          child: const Text('Join', style: TextStyle(fontSize: 12)),
        ),
      ),
    );
  }
}

/// ============================================================================
/// 6. SECTION 3: CREATE STUDIO
/// ============================================================================

class CreateSection extends StatelessWidget {
  const CreateSection({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Create Studio', style: TextStyle(fontWeight: FontWeight.bold)),
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          const Text(
            'Share Content & Build Communities',
            style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
          ),
          const SizedBox(height: 6),
          const Text(
            'Create a post, share a 24-hour story, launch a collaborative community, or propose a decision poll.',
            style: TextStyle(fontSize: 13, color: Colors.grey),
          ),
          const SizedBox(height: 20),
          _buildCreateOption(
            icon: Icons.post_add_rounded,
            title: 'Create Timeline Post',
            subtitle: 'Share text, photos, and project updates to your followers.',
            accent: WeChatColors.primaryOrange,
          ),
          const SizedBox(height: 12),
          _buildCreateOption(
            icon: Icons.auto_stories_rounded,
            title: 'Share 24h Story / Moment',
            subtitle: 'Publish quick visual updates with custom background accents.',
            accent: WeChatColors.secondaryBlue,
          ),
          const SizedBox(height: 12),
          _buildCreateOption(
            icon: Icons.groups_3_rounded,
            title: 'Launch a New Community',
            subtitle: 'Establish private or public groups with moderation guidelines.',
            accent: WeChatColors.primaryOrange,
          ),
          const SizedBox(height: 12),
          _buildCreateOption(
            icon: Icons.how_to_vote_rounded,
            title: 'Propose Group Decision / Poll',
            subtitle: 'Gather structured cryptographic consensus on ideas and choices.',
            accent: WeChatColors.secondaryBlue,
          ),
        ],
      ),
    );
  }

  Widget _buildCreateOption({
    required IconData icon,
    required String title,
    required String subtitle,
    required Color accent,
  }) {
    return Card(
      child: ListTile(
        contentPadding: const EdgeInsets.all(16),
        leading: CircleAvatar(
          radius: 24,
          backgroundColor: accent.withOpacity(0.15),
          child: Icon(icon, color: accent, size: 26),
        ),
        title: Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
        subtitle: Padding(
          padding: const EdgeInsets.only(top: 4),
          child: Text(subtitle, style: const TextStyle(fontSize: 12, height: 1.3)),
        ),
        trailing: const Icon(Icons.arrow_forward_ios_rounded, size: 16),
        onTap: () {},
      ),
    );
  }
}

/// ============================================================================
/// 7. SECTION 4: MESSAGES & COMMUNICATIONS
/// ============================================================================

/// ============================================================================
/// 7. SECTION 4: MESSAGES & COMMUNICATIONS (CHAT LIST & DIRECT CONVERSATIONS)
/// Styled strictly with official brand colors: Primary Orange (#E15712), Secondary Blue (#2FA0E1)
/// ============================================================================

/// Delivery status for message receipts
enum MessageDeliveryStatus {
  sending,
  sent,
  delivered,
  read,
}

/// Rich media and snippet classification for last message previews
enum MessageTypeSnippet {
  text,
  voice,
  image,
  document,
  location,
}

/// Message model for full conversation threads
class ChatMessageItem {
  final String id;
  final String text;
  final String timestamp;
  final bool isMe;
  final MessageDeliveryStatus status;
  final MessageTypeSnippet type;
  final String? attachmentName;
  final String? audioDuration;

  ChatMessageItem({
    required this.id,
    required this.text,
    required this.timestamp,
    required this.isMe,
    this.status = MessageDeliveryStatus.read,
    this.type = MessageTypeSnippet.text,
    this.attachmentName,
    this.audioDuration,
  });
}

/// Active Conversation item model for the Chat List
class ChatConversation {
  final String id;
  final String name;
  final String handle;
  final String avatarLetter;
  final Color avatarColor;
  String lastMessage;
  String timestamp;
  int unreadCount;
  bool isOnline;
  final bool isGroup;
  bool isPinned;
  bool isMuted;
  final bool isVerified;
  bool isLastMessageFromMe;
  MessageDeliveryStatus status;
  MessageTypeSnippet type;
  bool isTyping;
  List<ChatMessageItem> messages;

  ChatConversation({
    required this.id,
    required this.name,
    required this.handle,
    required this.avatarLetter,
    required this.avatarColor,
    required this.lastMessage,
    required this.timestamp,
    this.unreadCount = 0,
    this.isOnline = false,
    this.isGroup = false,
    this.isPinned = false,
    this.isMuted = false,
    this.isVerified = false,
    this.isLastMessageFromMe = false,
    this.status = MessageDeliveryStatus.delivered,
    this.type = MessageTypeSnippet.text,
    this.isTyping = false,
    required this.messages,
  });
}

class MessagesSection extends StatefulWidget {
  const MessagesSection({super.key});

  @override
  State<MessagesSection> createState() => _MessagesSectionState();
}

class _MessagesSectionState extends State<MessagesSection> {
  final TextEditingController _searchController = TextEditingController();
  String _selectedFilter = 'All'; // All, Unread, Favourites, Groups
  String _searchQuery = '';
  bool _isSearching = false;

  late List<ChatConversation> _conversations;

  @override
  void initState() {
    super.initState();
    _initializeConversations();
    _searchController.addListener(() {
      setState(() {
        _searchQuery = _searchController.text.trim().toLowerCase();
      });
    });
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  void _initializeConversations() {
    _conversations = [
      ChatConversation(
        id: 'conv_1',
        name: 'Alex Chen',
        handle: '@alex_chen',
        avatarLetter: 'A',
        avatarColor: WeChatColors.primaryOrange,
        lastMessage: "Let's review the new group decision feature shortly.",
        timestamp: '12:45',
        unreadCount: 0,
        isOnline: true,
        isGroup: false,
        isPinned: true,
        isMuted: false,
        isVerified: true,
        isLastMessageFromMe: true,
        status: MessageDeliveryStatus.read, // Double blue ticks (#2FA0E1)
        type: MessageTypeSnippet.text,
        isTyping: false,
        messages: [
          ChatMessageItem(
            id: 'm1_1',
            text: 'Hey Ambrose, did you push the latest security verification update?',
            timestamp: '12:30',
            isMe: false,
            status: MessageDeliveryStatus.read,
          ),
          ChatMessageItem(
            id: 'm1_2',
            text: 'Yes! SHA-256 cryptographic salts and JWT token refresh are live.',
            timestamp: '12:38',
            isMe: true,
            status: MessageDeliveryStatus.read,
          ),
          ChatMessageItem(
            id: 'm1_3',
            text: "Let's review the new group decision feature shortly.",
            timestamp: '12:45',
            isMe: true,
            status: MessageDeliveryStatus.read,
          ),
        ],
      ),
      ChatConversation(
        id: 'conv_2',
        name: 'Sophia Vance',
        handle: '@sophia_v',
        avatarLetter: 'S',
        avatarColor: WeChatColors.secondaryBlue,
        lastMessage: 'WeChat_Component_Specs_v2.pdf',
        timestamp: '11:20',
        unreadCount: 2,
        isOnline: true,
        isGroup: false,
        isPinned: true,
        isMuted: false,
        isVerified: true,
        isLastMessageFromMe: false,
        status: MessageDeliveryStatus.delivered,
        type: MessageTypeSnippet.document,
        isTyping: false,
        messages: [
          ChatMessageItem(
            id: 'm2_1',
            text: 'Finished the design guidelines and official color contrast ratios.',
            timestamp: '11:15',
            isMe: false,
            status: MessageDeliveryStatus.read,
          ),
          ChatMessageItem(
            id: 'm2_2',
            text: 'WeChat_Component_Specs_v2.pdf',
            timestamp: '11:20',
            isMe: false,
            status: MessageDeliveryStatus.read,
            type: MessageTypeSnippet.document,
            attachmentName: 'WeChat_Component_Specs_v2.pdf (4.8 MB)',
          ),
        ],
      ),
      ChatConversation(
        id: 'conv_3',
        name: 'WeChat Core Engineering',
        handle: '@wechat_core',
        avatarLetter: 'W',
        avatarColor: WeChatColors.primaryOrangeDark,
        lastMessage: 'Marcus: Zero build errors on release candidate! 🎉',
        timestamp: '09:15',
        unreadCount: 4,
        isOnline: false,
        isGroup: true,
        isPinned: false,
        isMuted: false,
        isVerified: true,
        isLastMessageFromMe: false,
        status: MessageDeliveryStatus.delivered,
        type: MessageTypeSnippet.text,
        isTyping: false,
        messages: [
          ChatMessageItem(
            id: 'm3_1',
            text: 'Team, please run the Robolectric suite before merging to main.',
            timestamp: '08:50',
            isMe: false,
          ),
          ChatMessageItem(
            id: 'm3_2',
            text: 'Marcus: Zero build errors on release candidate! 🎉',
            timestamp: '09:15',
            isMe: false,
          ),
        ],
      ),
      ChatConversation(
        id: 'conv_4',
        name: 'David Kim',
        handle: '@davidk',
        avatarLetter: 'D',
        avatarColor: WeChatColors.secondaryBlueDark,
        lastMessage: 'Voice message (0:34)',
        timestamp: 'Yesterday',
        unreadCount: 0,
        isOnline: true,
        isGroup: false,
        isPinned: false,
        isMuted: false,
        isVerified: false,
        isLastMessageFromMe: true,
        status: MessageDeliveryStatus.read, // Double blue ticks (#2FA0E1)
        type: MessageTypeSnippet.voice,
        isTyping: false,
        messages: [
          ChatMessageItem(
            id: 'm4_1',
            text: 'Voice message (0:34)',
            timestamp: 'Yesterday, 17:10',
            isMe: true,
            status: MessageDeliveryStatus.read,
            type: MessageTypeSnippet.voice,
            audioDuration: '0:34',
          ),
        ],
      ),
      ChatConversation(
        id: 'conv_5',
        name: 'Elena Rostova',
        handle: '@elena_r',
        avatarLetter: 'E',
        avatarColor: const Color(0xFF9C27B0),
        lastMessage: 'Photo (2 items)',
        timestamp: 'Nov 8',
        unreadCount: 0,
        isOnline: false,
        isGroup: false,
        isPinned: false,
        isMuted: false,
        isVerified: true,
        isLastMessageFromMe: true,
        status: MessageDeliveryStatus.delivered, // Double grey ticks
        type: MessageTypeSnippet.image,
        isTyping: false,
        messages: [
          ChatMessageItem(
            id: 'm5_1',
            text: 'Photo (2 items)',
            timestamp: 'Nov 8, 14:02',
            isMe: true,
            status: MessageDeliveryStatus.delivered,
            type: MessageTypeSnippet.image,
            attachmentName: 'Prototype_Mockups.png',
          ),
        ],
      ),
      ChatConversation(
        id: 'conv_6',
        name: 'Global Founders & Creators',
        handle: '@founders_circle',
        avatarLetter: 'G',
        avatarColor: WeChatColors.secondaryBlue,
        lastMessage: 'Ambrose: Live voting open on community governance rules.',
        timestamp: 'Nov 7',
        unreadCount: 0,
        isOnline: false,
        isGroup: true,
        isPinned: false,
        isMuted: true,
        isVerified: true,
        isLastMessageFromMe: true,
        status: MessageDeliveryStatus.sent, // Single grey tick
        type: MessageTypeSnippet.text,
        isTyping: false,
        messages: [
          ChatMessageItem(
            id: 'm6_1',
            text: 'Ambrose: Live voting open on community governance rules.',
            timestamp: 'Nov 7, 10:22',
            isMe: true,
            status: MessageDeliveryStatus.sent,
          ),
        ],
      ),
      ChatConversation(
        id: 'conv_7',
        name: 'WeChat Trust & Security',
        handle: '@wechat_security',
        avatarLetter: 'T',
        avatarColor: WeChatColors.primaryOrange,
        lastMessage: 'Your Digital Identity Card cryptographic key is verified (98% Trust Score).',
        timestamp: 'Nov 5',
        unreadCount: 0,
        isOnline: true,
        isGroup: false,
        isPinned: false,
        isMuted: false,
        isVerified: true,
        isLastMessageFromMe: false,
        status: MessageDeliveryStatus.read,
        type: MessageTypeSnippet.text,
        isTyping: false,
        messages: [
          ChatMessageItem(
            id: 'm7_1',
            text: 'Your Digital Identity Card cryptographic key is verified (98% Trust Score).',
            timestamp: 'Nov 5, 08:00',
            isMe: false,
          ),
        ],
      ),
    ];
  }

  List<ChatConversation> get _filteredConversations {
    return _conversations.where((conv) {
      // 1. Category filter
      if (_selectedFilter == 'Unread' && conv.unreadCount == 0) return false;
      if (_selectedFilter == 'Favourites' && !conv.isPinned) return false;
      if (_selectedFilter == 'Groups' && !conv.isGroup) return false;

      // 2. Search query filter
      if (_searchQuery.isNotEmpty) {
        final matchesName = conv.name.toLowerCase().contains(_searchQuery);
        final matchesHandle = conv.handle.toLowerCase().contains(_searchQuery);
        final matchesSnippet = conv.lastMessage.toLowerCase().contains(_searchQuery);
        return matchesName || matchesHandle || matchesSnippet;
      }
      return true;
    }).toList()
      ..sort((a, b) {
        // Pinned conversations stay at the top
        if (a.isPinned && !b.isPinned) return -1;
        if (!a.isPinned && b.isPinned) return 1;
        return 0;
      });
  }

  Future<void> _refreshConversations() async {
    // Attempt network sync with Django backend
    try {
      final response = await WeChatNetworkService.instance.get(ApiEndpoints.conversations);
      if (response.isSuccess && response.data is List) {
        // Sync items if backend active
      }
    } catch (_) {
      // Graceful offline fallback
    }
    await Future.delayed(const Duration(milliseconds: 400));
    if (mounted) setState(() {});
  }

  void _openChatDetail(ChatConversation conversation) async {
    setState(() {
      conversation.unreadCount = 0;
    });

    await Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => ChatDetailScreen(
          conversation: conversation,
          onMessageSent: (newMessage) {
            setState(() {
              conversation.lastMessage = newMessage.text;
              conversation.timestamp = 'Just now';
              conversation.isLastMessageFromMe = true;
              conversation.status = MessageDeliveryStatus.sent;
              conversation.type = newMessage.type;
              // Move to top of conversations list
              _conversations.remove(conversation);
              _conversations.insert(0, conversation);
            });
          },
        ),
      ),
    );
    if (mounted) setState(() {});
  }

  void _showConversationOptions(ChatConversation conversation) {
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (context) {
        return SafeArea(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Container(
                margin: const EdgeInsets.only(top: 10, bottom: 6),
                width: 36,
                height: 4,
                decoration: BoxDecoration(
                  color: Colors.grey.withOpacity(0.4),
                  borderRadius: BorderRadius.circular(2),
                ),
              ),
              ListTile(
                leading: CircleAvatar(
                  backgroundColor: conversation.avatarColor.withOpacity(0.15),
                  child: Text(
                    conversation.avatarLetter,
                    style: TextStyle(color: conversation.avatarColor, fontWeight: FontWeight.bold),
                  ),
                ),
                title: Text(conversation.name, style: const TextStyle(fontWeight: FontWeight.bold)),
                subtitle: Text(conversation.handle, style: const TextStyle(fontSize: 12, color: Colors.grey)),
              ),
              const Divider(height: 1),
              ListTile(
                leading: Icon(
                  conversation.isPinned ? Icons.push_pin_outlined : Icons.push_pin_rounded,
                  color: WeChatColors.primaryOrange,
                ),
                title: Text(conversation.isPinned ? 'Unpin chat' : 'Pin chat'),
                onTap: () {
                  Navigator.pop(context);
                  setState(() {
                    conversation.isPinned = !conversation.isPinned;
                  });
                },
              ),
              ListTile(
                leading: Icon(
                  conversation.unreadCount > 0 ? Icons.mark_chat_read_rounded : Icons.mark_chat_unread_rounded,
                  color: WeChatColors.secondaryBlue,
                ),
                title: Text(conversation.unreadCount > 0 ? 'Mark as read' : 'Mark as unread'),
                onTap: () {
                  Navigator.pop(context);
                  setState(() {
                    conversation.unreadCount = conversation.unreadCount > 0 ? 0 : 1;
                  });
                },
              ),
              ListTile(
                leading: Icon(
                  conversation.isMuted ? Icons.volume_up_rounded : Icons.volume_off_rounded,
                  color: Colors.grey,
                ),
                title: Text(conversation.isMuted ? 'Unmute notifications' : 'Mute notifications'),
                onTap: () {
                  Navigator.pop(context);
                  setState(() {
                    conversation.isMuted = !conversation.isMuted;
                  });
                },
              ),
              ListTile(
                leading: const Icon(Icons.delete_outline_rounded, color: Colors.red),
                title: const Text('Delete conversation', style: TextStyle(color: Colors.red)),
                onTap: () {
                  Navigator.pop(context);
                  setState(() {
                    _conversations.remove(conversation);
                  });
                },
              ),
            ],
          ),
        );
      },
    );
  }

  void _showNewChatDialog() {
    final nameController = TextEditingController();
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Row(
            children: [
              Icon(Icons.chat_bubble_rounded, color: WeChatColors.primaryOrange, size: 22),
              SizedBox(width: 8),
              Text('Start New Chat', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
            ],
          ),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const Text(
                'Enter username or handle to initiate an encrypted conversation channel.',
                style: TextStyle(fontSize: 13, color: Colors.grey),
              ),
              const SizedBox(height: 14),
              TextField(
                controller: nameController,
                autofocus: true,
                decoration: InputDecoration(
                  hintText: 'e.g. sarah_connor or @sarah',
                  prefixIcon: const Icon(Icons.alternate_email_rounded, color: WeChatColors.secondaryBlue, size: 20),
                  border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                  contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
                ),
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Cancel', style: TextStyle(color: Colors.grey)),
            ),
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: WeChatColors.primaryOrange,
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
              ),
              onPressed: () {
                final rawName = nameController.text.trim();
                if (rawName.isNotEmpty) {
                  Navigator.pop(context);
                  final newConv = ChatConversation(
                    id: 'conv_${DateTime.now().millisecondsSinceEpoch}',
                    name: rawName.replaceAll('@', ''),
                    handle: rawName.startsWith('@') ? rawName : '@$rawName',
                    avatarLetter: rawName.replaceAll('@', '').substring(0, 1).toUpperCase(),
                    avatarColor: WeChatColors.secondaryBlue,
                    lastMessage: 'Conversation initialized.',
                    timestamp: 'Just now',
                    unreadCount: 0,
                    isOnline: true,
                    isGroup: false,
                    isPinned: false,
                    isMuted: false,
                    isVerified: false,
                    isLastMessageFromMe: true,
                    status: MessageDeliveryStatus.sent,
                    messages: [
                      ChatMessageItem(
                        id: 'm_${DateTime.now().millisecondsSinceEpoch}',
                        text: 'Hello! Connected via WeChat secure messaging.',
                        timestamp: 'Just now',
                        isMe: true,
                        status: MessageDeliveryStatus.sent,
                      ),
                    ],
                  );
                  setState(() {
                    _conversations.insert(0, newConv);
                  });
                  _openChatDetail(newConv);
                }
              },
              child: const Text('Start Chat'),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final filtered = _filteredConversations;
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      appBar: AppBar(
        title: _isSearching
            ? TextField(
                controller: _searchController,
                autofocus: true,
                style: const TextStyle(fontSize: 16),
                decoration: InputDecoration(
                  hintText: 'Search chats, contacts, messages...',
                  border: InputBorder.none,
                  hintStyle: TextStyle(color: isDark ? Colors.grey[400] : Colors.grey[600]),
                ),
              )
            : Row(
                children: [
                  Container(
                    padding: const EdgeInsets.all(7),
                    decoration: BoxDecoration(
                      gradient: const LinearGradient(
                        colors: [WeChatColors.primaryOrange, WeChatColors.secondaryBlue],
                      ),
                      borderRadius: BorderRadius.circular(10),
                    ),
                    child: const Icon(Icons.chat_bubble_rounded, color: Colors.white, size: 18),
                  ),
                  const SizedBox(width: 10),
                  const Text('WeChat', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 20)),
                ],
              ),
        actions: [
          IconButton(
            icon: Icon(
              _isSearching ? Icons.close_rounded : Icons.search_rounded,
              color: WeChatColors.primaryOrange,
            ),
            onPressed: () {
              setState(() {
                if (_isSearching) {
                  _isSearching = false;
                  _searchController.clear();
                } else {
                  _isSearching = true;
                }
              });
            },
          ),
          IconButton(
            icon: const Icon(Icons.camera_alt_outlined),
            onPressed: () {},
          ),
          PopupMenuButton<String>(
            icon: const Icon(Icons.more_vert_rounded),
            onSelected: (value) {
              if (value == 'new_chat') _showNewChatDialog();
            },
            itemBuilder: (context) => [
              const PopupMenuItem(value: 'new_group', child: Text('New group')),
              const PopupMenuItem(value: 'new_community', child: Text('New community')),
              const PopupMenuItem(value: 'starred', child: Text('Starred messages')),
              const PopupMenuItem(value: 'new_chat', child: Text('New chat')),
              const PopupMenuItem(value: 'settings', child: Text('Settings')),
            ],
          ),
        ],
      ),
      body: RefreshIndicator(
        color: WeChatColors.primaryOrange,
        onRefresh: _refreshConversations,
        child: Column(
          children: [
            // Filter Chips Bar (All, Unread, Favourites, Groups)
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
              child: SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: Row(
                  children: [
                    _buildFilterChip('All', _conversations.length),
                    const SizedBox(width: 8),
                    _buildFilterChip('Unread', _conversations.where((c) => c.unreadCount > 0).length),
                    const SizedBox(width: 8),
                    _buildFilterChip('Favourites', _conversations.where((c) => c.isPinned).length),
                    const SizedBox(width: 8),
                    _buildFilterChip('Groups', _conversations.where((c) => c.isGroup).length),
                  ],
                ),
              ),
            ),
            const Divider(height: 1, thickness: 0.5),

            // Active Conversations List
            Expanded(
              child: filtered.isEmpty
                  ? Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(
                            Icons.chat_bubble_outline_rounded,
                            size: 64,
                            color: Colors.grey.withOpacity(0.5),
                          ),
                          const SizedBox(height: 14),
                          Text(
                            _searchQuery.isNotEmpty ? 'No conversations found' : 'No chats in this category',
                            style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                          ),
                          const SizedBox(height: 6),
                          Text(
                            _searchQuery.isNotEmpty
                                ? 'Try searching for another name or keyword.'
                                : 'Tap the floating button below to start a chat.',
                            style: const TextStyle(fontSize: 13, color: Colors.grey),
                          ),
                        ],
                      ),
                    )
                  : ListView.separated(
                      itemCount: filtered.length + 1, // +1 for archived row
                      separatorBuilder: (context, index) => const Divider(
                        height: 1,
                        indent: 76,
                        endIndent: 16,
                        thickness: 0.5,
                      ),
                      itemBuilder: (context, index) {
                        if (index == 0) {
                          return _buildArchivedRow(isDark);
                        }
                        final conv = filtered[index - 1];
                        return _buildConversationTile(conv, isDark);
                      },
                    ),
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        backgroundColor: WeChatColors.primaryOrange,
        foregroundColor: Colors.white,
        elevation: 3,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        onPressed: _showNewChatDialog,
        child: const Icon(Icons.chat_rounded),
      ),
    );
  }

  Widget _buildFilterChip(String label, int count) {
    final isSelected = _selectedFilter == label;
    return GestureDetector(
      onTap: () {
        setState(() {
          _selectedFilter = label;
        });
      },
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 200),
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 7),
        decoration: BoxDecoration(
          color: isSelected
              ? WeChatColors.primaryOrange
              : Theme.of(context).colorScheme.surfaceContainerHighest,
          borderRadius: BorderRadius.circular(20),
          border: Border.all(
            color: isSelected ? WeChatColors.primaryOrange : Colors.transparent,
          ),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              label,
              style: TextStyle(
                fontSize: 13,
                fontWeight: isSelected ? FontWeight.bold : FontWeight.w500,
                color: isSelected
                    ? Colors.white
                    : Theme.of(context).textTheme.bodyMedium?.color,
              ),
            ),
            if (count > 0 && label != 'All') ...[
              const SizedBox(width: 6),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 1.5),
                decoration: BoxDecoration(
                  color: isSelected
                      ? Colors.white.withOpacity(0.25)
                      : WeChatColors.primaryOrange.withOpacity(0.15),
                  borderRadius: BorderRadius.circular(10),
                ),
                child: Text(
                  '$count',
                  style: TextStyle(
                    fontSize: 11,
                    fontWeight: FontWeight.bold,
                    color: isSelected ? Colors.white : WeChatColors.primaryOrange,
                  ),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildArchivedRow(bool isDark) {
    return ListTile(
      dense: true,
      leading: Container(
        width: 44,
        alignment: Alignment.center,
        child: const Icon(Icons.archive_outlined, color: WeChatColors.secondaryBlue, size: 22),
      ),
      title: const Text(
        'Archived',
        style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14),
      ),
      trailing: const Text(
        '1',
        style: TextStyle(color: WeChatColors.secondaryBlue, fontSize: 12, fontWeight: FontWeight.bold),
      ),
      onTap: () {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Archived chats folder is empty.'),
            duration: Duration(seconds: 1),
          ),
        );
      },
    );
  }

  Widget _buildConversationTile(ChatConversation conv, bool isDark) {
    final hasUnread = conv.unreadCount > 0;

    return ListTile(
      onTap: () => _openChatDetail(conv),
      onLongPress: () => _showConversationOptions(conv),
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
      leading: Stack(
        clipBehavior: Clip.none,
        children: [
          CircleAvatar(
            radius: 25,
            backgroundColor: conv.avatarColor.withOpacity(0.18),
            child: conv.isGroup
                ? Icon(Icons.groups_rounded, color: conv.avatarColor, size: 26)
                : Text(
                    conv.avatarLetter,
                    style: TextStyle(
                      color: conv.avatarColor,
                      fontWeight: FontWeight.bold,
                      fontSize: 19,
                    ),
                  ),
          ),
          if (conv.isOnline)
            Positioned(
              right: 0,
              bottom: 0,
              child: Container(
                width: 13,
                height: 13,
                decoration: BoxDecoration(
                  color: const Color(0xFF00C853), // WhatsApp active online green
                  shape: BoxShape.circle,
                  border: Border.all(
                    color: isDark ? WeChatColors.darkSurface : WeChatColors.lightSurface,
                    width: 2.2,
                  ),
                ),
              ),
            ),
        ],
      ),
      title: Row(
        children: [
          Expanded(
            child: Row(
              children: [
                Flexible(
                  child: Text(
                    conv.name,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: TextStyle(
                      fontWeight: hasUnread ? FontWeight.bold : FontWeight.w600,
                      fontSize: 15.5,
                    ),
                  ),
                ),
                if (conv.isVerified) ...[
                  const SizedBox(width: 4),
                  const Icon(Icons.verified, color: WeChatColors.secondaryBlue, size: 15),
                ],
                if (conv.isMuted) ...[
                  const SizedBox(width: 4),
                  const Icon(Icons.volume_off_rounded, color: Colors.grey, size: 14),
                ],
              ],
            ),
          ),
          const SizedBox(width: 6),
          // Timestamp
          Text(
            conv.timestamp,
            style: TextStyle(
              fontSize: 11.5,
              fontWeight: hasUnread ? FontWeight.bold : FontWeight.normal,
              color: hasUnread ? WeChatColors.primaryOrange : Colors.grey,
            ),
          ),
        ],
      ),
      subtitle: Padding(
        padding: const EdgeInsets.only(top: 4),
        child: Row(
          children: [
            // Delivery / Read status indicator (only if last message sent by user)
            if (conv.isLastMessageFromMe) ...[
              _buildDeliveryStatusIcon(conv.status),
              const SizedBox(width: 4),
            ],
            // Type snippet icon (voice, photo, doc, etc.)
            if (conv.type != MessageTypeSnippet.text) ...[
              _buildTypeSnippetIcon(conv.type),
              const SizedBox(width: 4),
            ],
            // Message snippet text or typing indicator
            Expanded(
              child: conv.isTyping
                  ? const Text(
                      'typing...',
                      style: TextStyle(
                        fontSize: 13,
                        color: WeChatColors.primaryOrange,
                        fontWeight: FontWeight.bold,
                        fontStyle: FontStyle.italic,
                      ),
                    )
                  : Text(
                      conv.lastMessage,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: TextStyle(
                        fontSize: 13,
                        fontWeight: hasUnread ? FontWeight.w600 : FontWeight.normal,
                        color: hasUnread
                            ? (isDark ? Colors.white : Colors.black86)
                            : (isDark ? Colors.grey[400] : Colors.grey[600]),
                      ),
                    ),
            ),
            const SizedBox(width: 6),
            // Pinned indicator icon
            if (conv.isPinned) ...[
              const Icon(Icons.push_pin_rounded, size: 15, color: WeChatColors.primaryOrange),
              const SizedBox(width: 4),
            ],
            // Unread badge count
            if (hasUnread)
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 7, vertical: 2.5),
                decoration: BoxDecoration(
                  color: WeChatColors.primaryOrange,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  '${conv.unreadCount}',
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 11,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }

  /// Official Read Indicator Icon
  /// Single check: Sent (Grey)
  /// Double check: Delivered (Grey)
  /// Double check: Read (Secondary Blue #2FA0E1)
  Widget _buildDeliveryStatusIcon(MessageDeliveryStatus status) {
    switch (status) {
      case MessageDeliveryStatus.sending:
        return const Icon(Icons.access_time_rounded, size: 14, color: Colors.grey);
      case MessageDeliveryStatus.sent:
        return const Icon(Icons.check_rounded, size: 15, color: Colors.grey);
      case MessageDeliveryStatus.delivered:
        return const Icon(Icons.done_all_rounded, size: 15, color: Colors.grey);
      case MessageDeliveryStatus.read:
        return const Icon(Icons.done_all_rounded, size: 15, color: WeChatColors.secondaryBlue);
    }
  }

  Widget _buildTypeSnippetIcon(MessageTypeSnippet type) {
    switch (type) {
      case MessageTypeSnippet.voice:
        return const Icon(Icons.mic_rounded, size: 15, color: WeChatColors.primaryOrange);
      case MessageTypeSnippet.image:
        return const Icon(Icons.photo_camera_rounded, size: 15, color: WeChatColors.secondaryBlue);
      case MessageTypeSnippet.document:
        return const Icon(Icons.insert_drive_file_rounded, size: 15, color: WeChatColors.secondaryBlue);
      case MessageTypeSnippet.location:
        return const Icon(Icons.location_on_rounded, size: 15, color: Colors.redAccent);
      case MessageTypeSnippet.text:
        return const SizedBox.shrink();
    }
  }
}

/// ============================================================================
/// 7.1 CHAT DETAIL SCREEN (WHATSAPP-STYLE INTERACTIVE CONVERSATION)
/// ============================================================================

class ChatDetailScreen extends StatefulWidget {
  final ChatConversation conversation;
  final ValueChanged<ChatMessageItem> onMessageSent;

  const ChatDetailScreen({
    super.key,
    required this.conversation,
    required this.onMessageSent,
  });

  @override
  State<ChatDetailScreen> createState() => _ChatDetailScreenState();
}

class _ChatDetailScreenState extends State<ChatDetailScreen> {
  final TextEditingController _messageController = TextEditingController();
  final ScrollController _scrollController = ScrollController();
  bool _isComposing = false;

  @override
  void initState() {
    super.initState();
    _messageController.addListener(() {
      setState(() {
        _isComposing = _messageController.text.trim().isNotEmpty;
      });
    });
  }

  @override
  void dispose() {
    _messageController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  void _sendMessage({String? customText, MessageTypeSnippet type = MessageTypeSnippet.text, String? attachment}) {
    final textToSend = customText ?? _messageController.text.trim();
    if (textToSend.isEmpty) return;

    final now = DateTime.now();
    final timeStr = '${now.hour.toString().padLeft(2, '0')}:${now.minute.toString().padLeft(2, '0')}';

    final newItem = ChatMessageItem(
      id: 'msg_${DateTime.now().millisecondsSinceEpoch}',
      text: textToSend,
      timestamp: timeStr,
      isMe: true,
      status: MessageDeliveryStatus.sent,
      type: type,
      attachmentName: attachment,
    );

    setState(() {
      widget.conversation.messages.add(newItem);
    });

    _messageController.clear();
    widget.onMessageSent(newItem);

    // Scroll to bottom
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (_scrollController.hasClients) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 250),
          curve: Curves.easeOut,
        );
      }
    });

    // Simulate double ticks / read receipts after 1 second
    Future.delayed(const Duration(seconds: 1), () {
      if (mounted) {
        setState(() {
          widget.conversation.status = MessageDeliveryStatus.read;
        });
      }
    });
  }

  void _showAttachmentOptions() {
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (context) {
        return SafeArea(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 20),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    _buildAttachmentItem(
                      icon: Icons.insert_drive_file_rounded,
                      label: 'Document',
                      color: WeChatColors.secondaryBlue,
                      onTap: () {
                        Navigator.pop(context);
                        _sendMessage(
                          customText: 'Project_Overview_Draft.pdf',
                          type: MessageTypeSnippet.document,
                          attachment: 'Project_Overview_Draft.pdf (2.4 MB)',
                        );
                      },
                    ),
                    _buildAttachmentItem(
                      icon: Icons.photo_camera_rounded,
                      label: 'Camera',
                      color: WeChatColors.primaryOrange,
                      onTap: () {
                        Navigator.pop(context);
                        _sendMessage(
                          customText: 'Camera Snapshot',
                          type: MessageTypeSnippet.image,
                          attachment: 'photo_snapshot.jpg',
                        );
                      },
                    ),
                    _buildAttachmentItem(
                      icon: Icons.image_rounded,
                      label: 'Gallery',
                      color: const Color(0xFFE91E63),
                      onTap: () {
                        Navigator.pop(context);
                        _sendMessage(
                          customText: 'Photo from Gallery',
                          type: MessageTypeSnippet.image,
                          attachment: 'gallery_image.jpg',
                        );
                      },
                    ),
                  ],
                ),
                const SizedBox(height: 20),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    _buildAttachmentItem(
                      icon: Icons.headset_rounded,
                      label: 'Audio',
                      color: const Color(0xFFFF9800),
                      onTap: () {
                        Navigator.pop(context);
                        _sendMessage(
                          customText: 'Voice note (0:42)',
                          type: MessageTypeSnippet.voice,
                        );
                      },
                    ),
                    _buildAttachmentItem(
                      icon: Icons.location_on_rounded,
                      label: 'Location',
                      color: const Color(0xFF00C853),
                      onTap: () {
                        Navigator.pop(context);
                        _sendMessage(
                          customText: 'Shared live location (WeChat Headquarters)',
                          type: MessageTypeSnippet.location,
                        );
                      },
                    ),
                    _buildAttachmentItem(
                      icon: Icons.poll_rounded,
                      label: 'Poll',
                      color: WeChatColors.secondaryBlueDark,
                      onTap: () {
                        Navigator.pop(context);
                        _sendMessage(
                          customText: 'Poll: Which deployment window works best?',
                          type: MessageTypeSnippet.text,
                        );
                      },
                    ),
                  ],
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildAttachmentItem({
    required IconData icon,
    required String label,
    required Color color,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Column(
        children: [
          CircleAvatar(
            radius: 26,
            backgroundColor: color.withOpacity(0.15),
            child: Icon(icon, color: color, size: 26),
          ),
          const SizedBox(height: 6),
          Text(label, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w500)),
        ],
      ),
    );
  }

  void _showSmartMessageAction(ChatMessageItem message) {
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(20))),
      builder: (context) {
        return SafeArea(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Container(
                margin: const EdgeInsets.only(top: 10, bottom: 6),
                width: 36,
                height: 4,
                decoration: BoxDecoration(
                  color: Colors.grey.withOpacity(0.4),
                  borderRadius: BorderRadius.circular(2),
                ),
              ),
              const Padding(
                padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                child: Row(
                  children: [
                    Icon(Icons.bolt_rounded, color: WeChatColors.primaryOrange, size: 20),
                    SizedBox(width: 8),
                    Text('Smart WeChat Actions', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                  ],
                ),
              ),
              const Divider(height: 1),
              ListTile(
                leading: const Icon(Icons.check_box_outlined, color: WeChatColors.primaryOrange),
                title: const Text('Convert to Life Dashboard Task'),
                subtitle: const Text('Adds task to personal productivity queue.', style: TextStyle(fontSize: 11)),
                onTap: () {
                  Navigator.pop(context);
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text('Added "${message.text}" to Personal Tasks.'),
                      backgroundColor: WeChatColors.primaryOrange,
                    ),
                  );
                },
              ),
              ListTile(
                leading: const Icon(Icons.alarm_add_rounded, color: WeChatColors.secondaryBlue),
                title: const Text('Set 2-Hour Reminder'),
                subtitle: const Text('Triggers notification to follow up on this message.', style: TextStyle(fontSize: 11)),
                onTap: () {
                  Navigator.pop(context);
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('Reminder set for 2 hours from now.'),
                      backgroundColor: WeChatColors.secondaryBlue,
                    ),
                  );
                },
              ),
              ListTile(
                leading: const Icon(Icons.bookmark_border_rounded, color: Colors.grey),
                title: const Text('Save to Universal Content Library'),
                onTap: () {
                  Navigator.pop(context);
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Saved to your universal resource library.')),
                  );
                },
              ),
            ],
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final conv = widget.conversation;

    return Scaffold(
      appBar: AppBar(
        titleSpacing: 0,
        title: Row(
          children: [
            CircleAvatar(
              radius: 19,
              backgroundColor: conv.avatarColor.withOpacity(0.2),
              child: conv.isGroup
                  ? Icon(Icons.groups_rounded, color: conv.avatarColor, size: 20)
                  : Text(conv.avatarLetter, style: TextStyle(color: conv.avatarColor, fontWeight: FontWeight.bold)),
            ),
            const SizedBox(width: 10),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Flexible(
                        child: Text(
                          conv.name,
                          overflow: TextOverflow.ellipsis,
                          style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                        ),
                      ),
                      if (conv.isVerified) ...[
                        const SizedBox(width: 4),
                        const Icon(Icons.verified, color: WeChatColors.secondaryBlue, size: 14),
                      ],
                    ],
                  ),
                  Text(
                    conv.isOnline ? 'online' : 'last seen recently',
                    style: TextStyle(
                      fontSize: 11,
                      color: conv.isOnline ? const Color(0xFF00C853) : Colors.grey,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.videocam_rounded, color: WeChatColors.primaryOrange),
            onPressed: () {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('Initiating encrypted video call with ${conv.name}...')),
              );
            },
          ),
          IconButton(
            icon: const Icon(Icons.call_rounded, color: WeChatColors.primaryOrange),
            onPressed: () {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('Initiating encrypted audio call with ${conv.name}...')),
              );
            },
          ),
          PopupMenuButton<String>(
            icon: const Icon(Icons.more_vert_rounded),
            itemBuilder: (context) => [
              const PopupMenuItem(value: 'view_contact', child: Text('View contact / ID Card')),
              const PopupMenuItem(value: 'media', child: Text('Media, links, and docs')),
              const PopupMenuItem(value: 'search', child: Text('Search in chat')),
              const PopupMenuItem(value: 'mute', child: Text('Mute notifications')),
              const PopupMenuItem(value: 'clear', child: Text('Clear chat')),
            ],
          ),
        ],
      ),
      body: Container(
        color: isDark ? WeChatColors.darkBackground : const Color(0xFFF0F2F5),
        child: Column(
          children: [
            // Cryptographic Security Banner
            Container(
              margin: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
              decoration: BoxDecoration(
                color: WeChatColors.secondaryBlue.withOpacity(0.12),
                borderRadius: BorderRadius.circular(10),
                border: Border.all(color: WeChatColors.secondaryBlue.withOpacity(0.3)),
              ),
              child: const Row(
                children: [
                  Icon(Icons.lock_rounded, size: 14, color: WeChatColors.secondaryBlue),
                  SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      'Messages are end-to-end encrypted with zero-trust credentials.',
                      style: TextStyle(fontSize: 11, color: WeChatColors.secondaryBlueDark),
                    ),
                  ),
                ],
              ),
            ),

            // Messages Stream
            Expanded(
              child: ListView.builder(
                controller: _scrollController,
                padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                itemCount: conv.messages.length,
                itemBuilder: (context, index) {
                  final msg = conv.messages[index];
                  return _buildMessageBubble(msg, isDark);
                },
              ),
            ),

            // Message Composer Bar
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 8),
              decoration: BoxDecoration(
                color: Theme.of(context).colorScheme.surface,
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.05),
                    blurRadius: 6,
                    offset: const Offset(0, -2),
                  ),
                ],
              ),
              child: SafeArea(
                child: Row(
                  children: [
                    // Emoji button
                    IconButton(
                      icon: const Icon(Icons.emoji_emotions_outlined, color: Colors.grey),
                      onPressed: () {},
                    ),
                    // Attachment button
                    IconButton(
                      icon: const Icon(Icons.attach_file_rounded, color: Colors.grey),
                      onPressed: _showAttachmentOptions,
                    ),
                    // Text Input Field
                    Expanded(
                      child: Container(
                        padding: const EdgeInsets.symmetric(horizontal: 14),
                        decoration: BoxDecoration(
                          color: Theme.of(context).colorScheme.surfaceContainerHighest,
                          borderRadius: BorderRadius.circular(22),
                        ),
                        child: TextField(
                          controller: _messageController,
                          maxLines: 4,
                          minLines: 1,
                          decoration: const InputDecoration(
                            hintText: 'Message',
                            border: InputBorder.none,
                            contentPadding: EdgeInsets.symmetric(vertical: 10),
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(width: 6),
                    // Send or Mic button
                    CircleAvatar(
                      radius: 22,
                      backgroundColor: WeChatColors.primaryOrange,
                      child: IconButton(
                        icon: Icon(
                          _isComposing ? Icons.send_rounded : Icons.mic_rounded,
                          color: Colors.white,
                          size: 19,
                        ),
                        onPressed: () {
                          if (_isComposing) {
                            _sendMessage();
                          } else {
                            _sendMessage(
                              customText: 'Voice note (0:15)',
                              type: MessageTypeSnippet.voice,
                            );
                          }
                        },
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildMessageBubble(ChatMessageItem msg, bool isDark) {
    final isMe = msg.isMe;

    return GestureDetector(
      onLongPress: () => _showSmartMessageAction(msg),
      child: Align(
        alignment: isMe ? Alignment.centerRight : Alignment.centerLeft,
        child: Container(
          margin: const EdgeInsets.symmetric(vertical: 4),
          constraints: BoxConstraints(maxWidth: MediaQuery.of(context).size.width * 0.78),
          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 9),
          decoration: BoxDecoration(
            color: isMe
                ? (isDark ? WeChatColors.primaryOrangeDark : const Color(0xFFFFECE2))
                : (isDark ? WeChatColors.darkSurface : Colors.white),
            borderRadius: BorderRadius.only(
              topLeft: const Radius.circular(16),
              topRight: const Radius.circular(16),
              bottomLeft: isMe ? const Radius.circular(16) : const Radius.circular(4),
              bottomRight: isMe ? const Radius.circular(4) : const Radius.circular(16),
            ),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.04),
                blurRadius: 4,
                offset: const Offset(0, 1),
              ),
            ],
            border: isMe
                ? null
                : Border.all(color: isDark ? WeChatColors.darkBorder : Colors.black12, width: 0.5),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Media Content if applicable
              if (msg.type == MessageTypeSnippet.document) ...[
                Container(
                  padding: const EdgeInsets.all(8),
                  decoration: BoxDecoration(
                    color: WeChatColors.secondaryBlue.withOpacity(0.12),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      const Icon(Icons.insert_drive_file_rounded, color: WeChatColors.secondaryBlue, size: 24),
                      const SizedBox(width: 8),
                      Flexible(
                        child: Text(
                          msg.attachmentName ?? msg.text,
                          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13),
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 6),
              ] else if (msg.type == MessageTypeSnippet.voice) ...[
                Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    CircleAvatar(
                      radius: 16,
                      backgroundColor: WeChatColors.primaryOrange,
                      child: const Icon(Icons.play_arrow_rounded, color: Colors.white, size: 20),
                    ),
                    const SizedBox(width: 10),
                    const Icon(Icons.graphic_eq_rounded, color: WeChatColors.primaryOrange, size: 28),
                    const SizedBox(width: 6),
                    Text(msg.audioDuration ?? '0:34', style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                  ],
                ),
                const SizedBox(height: 4),
              ] else if (msg.type == MessageTypeSnippet.location) ...[
                Container(
                  padding: const EdgeInsets.all(8),
                  decoration: BoxDecoration(
                    color: Colors.red.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: const Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Icon(Icons.location_on_rounded, color: Colors.redAccent, size: 22),
                      SizedBox(width: 6),
                      Text('Pinned Live Location', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                    ],
                  ),
                ),
                const SizedBox(height: 6),
              ],

              // Message Text
              Text(
                msg.text,
                style: TextStyle(
                  fontSize: 14.5,
                  color: isMe
                      ? (isDark ? Colors.white : Colors.black86)
                      : (isDark ? Colors.white : Colors.black86),
                ),
              ),
              const SizedBox(height: 4),

              // Timestamp & Read Indicator
              Row(
                mainAxisSize: MainAxisSize.min,
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  Text(
                    msg.timestamp,
                    style: TextStyle(
                      fontSize: 10.5,
                      color: isDark ? Colors.grey[400] : Colors.grey[600],
                    ),
                  ),
                  if (isMe) ...[
                    const SizedBox(width: 4),
                    _buildDeliveryStatusIcon(msg.status),
                  ],
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildDeliveryStatusIcon(MessageDeliveryStatus status) {
    switch (status) {
      case MessageDeliveryStatus.sending:
        return const Icon(Icons.access_time_rounded, size: 13, color: Colors.grey);
      case MessageDeliveryStatus.sent:
        return const Icon(Icons.check_rounded, size: 14, color: Colors.grey);
      case MessageDeliveryStatus.delivered:
        return const Icon(Icons.done_all_rounded, size: 14, color: Colors.grey);
      case MessageDeliveryStatus.read:
        return const Icon(Icons.done_all_rounded, size: 14, color: WeChatColors.secondaryBlue);
    }
  }
}

/// ============================================================================
/// 8. SECTION 5: PROFILE & DIGITAL IDENTITY
/// ============================================================================

class ProfileSection extends StatelessWidget {
  const ProfileSection({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('My Profile', style: TextStyle(fontWeight: FontWeight.bold)),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings_outlined),
            onPressed: () {},
          ),
        ],
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          // Profile Header Card
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                children: [
                  CircleAvatar(
                    radius: 36,
                    backgroundColor: WeChatColors.primaryOrange,
                    child: const Text('ME', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 20)),
                  ),
                  const SizedBox(height: 12),
                  const Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text('Ambrose Onyok', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
                      SizedBox(width: 4),
                      Icon(Icons.verified, color: WeChatColors.primaryOrange, size: 18),
                    ],
                  ),
                  const SizedBox(height: 2),
                  const Text('@ambrose • Verified Member', style: TextStyle(color: WeChatColors.secondaryBlue, fontSize: 12)),
                  const SizedBox(height: 16),
                  const Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      _StatColumn('Posts', '18'),
                      _StatColumn('Followers', '320'),
                      _StatColumn('Following', '140'),
                      _StatColumn('Trust Score', '98%'),
                    ],
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          // Digital ID Card
          Card(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(16),
              side: const BorderSide(color: WeChatColors.primaryOrange, width: 1),
            ),
            child: ListTile(
              leading: const Icon(Icons.badge_rounded, color: WeChatColors.primaryOrange, size: 28),
              title: const Text('Digital Identity Card', style: TextStyle(fontWeight: FontWeight.bold)),
              subtitle: const Text('View and share cryptographic profile QR & contact details.', style: TextStyle(fontSize: 12)),
              trailing: const Icon(Icons.qr_code_2_rounded, color: WeChatColors.secondaryBlue),
              onTap: () {},
            ),
          ),
          const SizedBox(height: 12),

          // Personal Life Dashboard
          Card(
            child: ListTile(
              leading: const Icon(Icons.dashboard_customize_rounded, color: WeChatColors.secondaryBlue, size: 28),
              title: const Text('Personal Life Dashboard', style: TextStyle(fontWeight: FontWeight.bold)),
              subtitle: const Text('Reminders, tasks, saved messages and universal library.', style: TextStyle(fontSize: 12)),
              trailing: const Icon(Icons.arrow_forward_ios_rounded, size: 14),
              onTap: () {},
            ),
          ),
          const SizedBox(height: 12),

          // Security & Active Sessions
          Card(
            child: ListTile(
              leading: const Icon(Icons.security_rounded, color: WeChatColors.primaryOrange, size: 28),
              title: const Text('Security & Session Management', style: TextStyle(fontWeight: FontWeight.bold)),
              subtitle: const Text('Active devices, two-factor auth and encryption keys.', style: TextStyle(fontSize: 12)),
              trailing: const Icon(Icons.arrow_forward_ios_rounded, size: 14),
              onTap: () {},
            ),
          ),
        ],
      ),
    );
  }
}

class _StatColumn extends StatelessWidget {
  final String label;
  final String value;
  const _StatColumn(this.label, this.value);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text(value, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
        const SizedBox(height: 2),
        Text(label, style: const TextStyle(fontSize: 11, color: Colors.grey)),
      ],
    );
  }
}

/// ============================================================================
/// 9. WECHAT NETWORK SERVICE & DJANGO API CLIENT
/// Configurable Base URL, Token Interceptors, 401 Auto-Refresh, Multipart Uploads
/// ============================================================================

/// Configuration constants for Django backend on Render
class NetworkConfig {
  static String baseUrl = 'https://wechat-backend-api.onrender.com/api/v1';
  static String wsBaseUrl = 'wss://wechat-backend-api.onrender.com/ws';

  static const Duration connectTimeout = Duration(seconds: 15);
  static const Duration receiveTimeout = Duration(seconds: 20);

  static void setBaseUrl(String url) {
    baseUrl = url.endsWith('/') ? url.substring(0, url.length - 1) : url;
  }
}

/// Token storage and lifecycle manager
class TokenManager {
  static final TokenManager _instance = TokenManager._internal();
  factory TokenManager() => _instance;
  TokenManager._internal();

  String? _accessToken;
  String? _refreshToken;
  DateTime? _tokenExpiry;

  String? get accessToken => _accessToken;
  String? get refreshToken => _refreshToken;
  bool get hasValidToken => _accessToken != null && !isExpired;

  bool get isExpired {
    if (_tokenExpiry == null) return false;
    return DateTime.now().isAfter(_tokenExpiry!);
  }

  void saveTokens({
    required String access,
    required String refresh,
    int? expiresInSeconds,
  }) {
    _accessToken = access;
    _refreshToken = refresh;
    if (expiresInSeconds != null) {
      _tokenExpiry = DateTime.now().add(Duration(seconds: expiresInSeconds));
    }
  }

  void updateAccessToken(String newAccess, {int? expiresInSeconds}) {
    _accessToken = newAccess;
    if (expiresInSeconds != null) {
      _tokenExpiry = DateTime.now().add(Duration(seconds: expiresInSeconds));
    }
  }

  void clearTokens() {
    _accessToken = null;
    _refreshToken = null;
    _tokenExpiry = null;
  }
}

/// Strongly-typed network response model
class ApiResponse<T> {
  final int statusCode;
  final bool isSuccess;
  final T? data;
  final String? errorMessage;
  final Map<String, dynamic>? fieldErrors;

  ApiResponse({
    required this.statusCode,
    required this.isSuccess,
    this.data,
    this.errorMessage,
    this.fieldErrors,
  });

  factory ApiResponse.success(int statusCode, T data) {
    return ApiResponse(
      statusCode: statusCode,
      isSuccess: true,
      data: data,
    );
  }

  factory ApiResponse.error(int statusCode, String message, {Map<String, dynamic>? fieldErrors}) {
    return ApiResponse(
      statusCode: statusCode,
      isSuccess: false,
      errorMessage: message,
      fieldErrors: fieldErrors,
    );
  }
}

/// Interceptor contract for request preprocessing and response handling
abstract class NetworkInterceptor {
  Future<Map<String, String>> onRequest(String url, String method, Map<String, String> headers);
  Future<void> onResponse(int statusCode, dynamic body);
  Future<bool> onError(int statusCode, dynamic body);
}

/// Default WeChat Token & Auth Interceptor
class WeChatAuthInterceptor implements NetworkInterceptor {
  final TokenManager tokenManager = TokenManager();
  bool _isRefreshing = false;

  @override
  Future<Map<String, String>> onRequest(
    String url,
    String method,
    Map<String, String> headers,
  ) async {
    final updatedHeaders = Map<String, String>.from(headers);
    updatedHeaders['Content-Type'] = 'application/json';
    updatedHeaders['Accept'] = 'application/json';
    updatedHeaders['X-WeChat-Platform'] = 'Android-Flutter';

    // Inject Bearer token if available
    final token = tokenManager.accessToken;
    if (token != null && token.isNotEmpty && !url.contains('/auth/token/refresh/')) {
      updatedHeaders['Authorization'] = 'Bearer $token';
    }

    if (kDebugMode) {
      debugPrint('[WeChat Network] --> $method $url');
    }
    return updatedHeaders;
  }

  @override
  Future<void> onResponse(int statusCode, dynamic body) async {
    if (kDebugMode) {
      debugPrint('[WeChat Network] <-- Response Status: $statusCode');
    }
  }

  @override
  Future<bool> onError(int statusCode, dynamic body) async {
    // Detect 401 Unauthorized for token refresh
    if (statusCode == 401 && tokenManager.refreshToken != null && !_isRefreshing) {
      _isRefreshing = true;
      try {
        final refreshSuccess = await WeChatNetworkService.instance.refreshToken();
        return refreshSuccess;
      } finally {
        _isRefreshing = false;
      }
    }
    return false;
  }
}

/// Primary Network Service communicating with Django REST Framework backend
class WeChatNetworkService {
  static final WeChatNetworkService _instance = WeChatNetworkService._internal();
  static WeChatNetworkService get instance => _instance;

  final HttpClient _httpClient = HttpClient();
  final TokenManager _tokenManager = TokenManager();
  final List<NetworkInterceptor> _interceptors = [WeChatAuthInterceptor()];

  WeChatNetworkService._internal() {
    _httpClient.connectionTimeout = NetworkConfig.connectTimeout;
  }

  void addInterceptor(NetworkInterceptor interceptor) {
    _interceptors.add(interceptor);
  }

  /// GET Request
  Future<ApiResponse<dynamic>> get(
    String endpoint, {
    Map<String, dynamic>? queryParameters,
    Map<String, String>? headers,
  }) async {
    return _sendRequest(
      method: 'GET',
      endpoint: endpoint,
      queryParameters: queryParameters,
      customHeaders: headers,
    );
  }

  /// POST Request
  Future<ApiResponse<dynamic>> post(
    String endpoint, {
    dynamic body,
    Map<String, dynamic>? queryParameters,
    Map<String, String>? headers,
  }) async {
    return _sendRequest(
      method: 'POST',
      endpoint: endpoint,
      body: body,
      queryParameters: queryParameters,
      customHeaders: headers,
    );
  }

  /// PUT Request
  Future<ApiResponse<dynamic>> put(
    String endpoint, {
    dynamic body,
    Map<String, String>? headers,
  }) async {
    return _sendRequest(
      method: 'PUT',
      endpoint: endpoint,
      body: body,
      customHeaders: headers,
    );
  }

  /// PATCH Request
  Future<ApiResponse<dynamic>> patch(
    String endpoint, {
    dynamic body,
    Map<String, String>? headers,
  }) async {
    return _sendRequest(
      method: 'PATCH',
      endpoint: endpoint,
      body: body,
      customHeaders: headers,
    );
  }

  /// DELETE Request
  Future<ApiResponse<dynamic>> delete(
    String endpoint, {
    Map<String, String>? headers,
  }) async {
    return _sendRequest(
      method: 'DELETE',
      endpoint: endpoint,
      customHeaders: headers,
    );
  }

  /// Core request executor with pipeline interceptors and automatic 401 refresh retry
  Future<ApiResponse<dynamic>> _sendRequest({
    required String method,
    required String endpoint,
    dynamic body,
    Map<String, dynamic>? queryParameters,
    Map<String, String>? customHeaders,
    bool isRetry = false,
  }) async {
    final uri = _buildUri(endpoint, queryParameters);
    var headers = customHeaders ?? <String, String>{};

    // Run request interceptors
    for (final interceptor in _interceptors) {
      headers = await interceptor.onRequest(uri.toString(), method, headers);
    }

    try {
      final request = await _httpClient.openUrl(method, uri);

      // Attach headers
      headers.forEach((key, value) {
        request.headers.set(key, value);
      });

      // Write body payload
      if (body != null) {
        final jsonString = jsonEncode(body);
        request.headers.contentType = ContentType.json;
        request.write(jsonString);
      }

      final response = await request.close().timeout(NetworkConfig.receiveTimeout);
      final responseBody = await response.transform(utf8.decoder).join();
      final statusCode = response.statusCode;

      dynamic parsedData;
      if (responseBody.isNotEmpty) {
        try {
          parsedData = jsonDecode(responseBody);
        } catch (_) {
          parsedData = responseBody;
        }
      }

      // Run response interceptors
      for (final interceptor in _interceptors) {
        await interceptor.onResponse(statusCode, parsedData);
      }

      // Check for token refresh on 401
      if (statusCode == 401 && !isRetry) {
        bool canRetry = false;
        for (final interceptor in _interceptors) {
          canRetry = await interceptor.onError(statusCode, parsedData) || canRetry;
        }
        if (canRetry) {
          // Replay original request with refreshed token
          return _sendRequest(
            method: method,
            endpoint: endpoint,
            body: body,
            queryParameters: queryParameters,
            customHeaders: customHeaders,
            isRetry: true,
          );
        }
      }

      if (statusCode >= 200 && statusCode < 300) {
        return ApiResponse.success(statusCode, parsedData);
      } else {
        return _handleErrorResponse(statusCode, parsedData);
      }
    } on SocketException catch (e) {
      return ApiResponse.error(0, 'Network connection error. Check server status at ${NetworkConfig.baseUrl}: ${e.message}');
    } on TimeoutException {
      return ApiResponse.error(408, 'Request timed out while connecting to Django backend.');
    } catch (e) {
      return ApiResponse.error(500, 'Unexpected network error: $e');
    }
  }

  /// Multipart File Upload (Profile pictures, videos, documents to Cloudinary via Django)
  Future<ApiResponse<dynamic>> uploadMultipart(
    String endpoint, {
    required String fieldName,
    required List<int> fileBytes,
    required String fileName,
    Map<String, String>? additionalFields,
  }) async {
    final uri = _buildUri(endpoint, null);
    final boundary = '----WeChatBoundary${DateTime.now().millisecondsSinceEpoch}';

    try {
      final request = await _httpClient.postUrl(uri);
      request.headers.set(HttpHeaders.contentTypeHeader, 'multipart/form-data; boundary=$boundary');

      final token = _tokenManager.accessToken;
      if (token != null) {
        request.headers.set(HttpHeaders.authorizationHeader, 'Bearer $token');
      }

      final boundaryBytes = utf8.encode('--$boundary\r\n');
      final endBoundaryBytes = utf8.encode('--$boundary--\r\n');

      // Write additional form fields
      if (additionalFields != null) {
        for (final entry in additionalFields.entries) {
          request.add(boundaryBytes);
          request.add(utf8.encode(
            'Content-Disposition: form-data; name="${entry.key}"\r\n\r\n${entry.value}\r\n',
          ));
        }
      }

      // Write file payload
      request.add(boundaryBytes);
      request.add(utf8.encode(
        'Content-Disposition: form-data; name="$fieldName"; filename="$fileName"\r\n'
        'Content-Type: application/octet-stream\r\n\r\n',
      ));
      request.add(fileBytes);
      request.add(utf8.encode('\r\n'));
      request.add(endBoundaryBytes);

      final response = await request.close().timeout(NetworkConfig.receiveTimeout);
      final responseBody = await response.transform(utf8.decoder).join();
      final statusCode = response.statusCode;

      dynamic parsedData;
      if (responseBody.isNotEmpty) {
        try {
          parsedData = jsonDecode(responseBody);
        } catch (_) {
          parsedData = responseBody;
        }
      }

      if (statusCode >= 200 && statusCode < 300) {
        return ApiResponse.success(statusCode, parsedData);
      } else {
        return _handleErrorResponse(statusCode, parsedData);
      }
    } catch (e) {
      return ApiResponse.error(500, 'Multipart upload failed: $e');
    }
  }

  /// Automatic token refresh handler calling Django JWT endpoint
  Future<bool> refreshToken() async {
    final refresh = _tokenManager.refreshToken;
    if (refresh == null) return false;

    try {
      final uri = _buildUri(ApiEndpoints.tokenRefresh, null);
      final request = await _httpClient.postUrl(uri);
      request.headers.contentType = ContentType.json;
      request.write(jsonEncode({'refresh': refresh}));

      final response = await request.close().timeout(NetworkConfig.receiveTimeout);
      if (response.statusCode == 200) {
        final body = await response.transform(utf8.decoder).join();
        final data = jsonDecode(body) as Map<String, dynamic>;
        final newAccess = data['access'] as String;
        _tokenManager.updateAccessToken(newAccess);
        if (kDebugMode) {
          debugPrint('[WeChat Network] Access token successfully refreshed.');
        }
        return true;
      } else {
        // Refresh token invalid or expired -> force logout
        _tokenManager.clearTokens();
        return false;
      }
    } catch (_) {
      return false;
    }
  }

  /// Parses Django REST Framework error formats
  ApiResponse<dynamic> _handleErrorResponse(int statusCode, dynamic data) {
    String errorMessage = 'Request failed with status $statusCode';
    Map<String, dynamic>? fieldErrors;

    if (data is Map<String, dynamic>) {
      if (data.containsKey('detail')) {
        errorMessage = data['detail'].toString();
      } else if (data.containsKey('error')) {
        errorMessage = data['error'].toString();
      } else if (data.containsKey('message')) {
        errorMessage = data['message'].toString();
      } else {
        // DRF field validation errors (e.g. {"username": ["Already taken"]})
        fieldErrors = data;
        final firstKey = data.keys.firstOrNull;
        if (firstKey != null) {
          final firstVal = data[firstKey];
          if (firstVal is List && firstVal.isNotEmpty) {
            errorMessage = '$firstKey: ${firstVal.first}';
          } else {
            errorMessage = '$firstKey: $firstVal';
          }
        }
      }
    }

    return ApiResponse.error(statusCode, errorMessage, fieldErrors: fieldErrors);
  }

  Uri _buildUri(String endpoint, Map<String, dynamic>? queryParameters) {
    final cleanEndpoint = endpoint.startsWith('/') ? endpoint : '/$endpoint';
    final fullUrl = '${NetworkConfig.baseUrl}$cleanEndpoint';
    final baseUri = Uri.parse(fullUrl);

    if (queryParameters != null && queryParameters.isNotEmpty) {
      final queryMap = queryParameters.map((key, value) => MapEntry(key, value.toString()));
      return baseUri.replace(queryParameters: queryMap);
    }
    return baseUri;
  }
}

/// Django REST Framework API Endpoint Registry
class ApiEndpoints {
  // Authentication & Sessions
  static const String register = '/auth/register/';
  static const String login = '/auth/login/';
  static const String tokenRefresh = '/auth/token/refresh/';
  static const String logout = '/auth/logout/';
  static const String profile = '/auth/profile/';
  static const String changePassword = '/auth/password/change/';
  static const String sessions = '/auth/sessions/';
  static const String deleteAccount = '/auth/account/delete/';

  // Social Timeline & Feed
  static const String posts = '/posts/';
  static String postDetail(String id) => '/posts/$id/';
  static String postLike(String id) => '/posts/$id/like/';
  static String postBookmark(String id) => '/posts/$id/bookmark/';
  static String postComments(String id) => '/posts/$id/comments/';
  static const String stories = '/stories/';

  // Communication & Chats
  static const String conversations = '/chat/conversations/';
  static String conversationMessages(String id) => '/chat/conversations/$id/messages/';
  static String messageReact(String id) => '/chat/messages/$id/react/';
  static const String callLogs = '/chat/calls/';

  // Communities & Decisions
  static const String communities = '/communities/';
  static String communityDetail(String id) => '/communities/$id/';
  static String communityJoin(String id) => '/communities/$id/join/';
  static const String polls = '/communities/polls/';
  static String pollVote(String id) => '/communities/polls/$id/vote/';

  // Life Dashboard
  static const String tasks = '/dashboard/tasks/';
  static String taskDetail(String id) => '/dashboard/tasks/$id/';
  static const String reminders = '/dashboard/reminders/';
  static const String library = '/dashboard/library/';
}


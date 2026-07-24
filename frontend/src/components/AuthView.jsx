import React, { useState } from 'react';
import { Wallet, ArrowRight, ShieldCheck, Zap, Send, Eye, EyeOff } from 'lucide-react';
import { api, setAuthToken, setUser } from '../api';

export default function AuthView({ onAuthSuccess }) {
  const [isLogin, setIsLogin] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      let res;
      if (isLogin) {
        res = await api.login(formData.email, formData.password);
      } else {
        res = await api.register(formData.firstName, formData.lastName, formData.email, formData.password);
      }

      const token = res.accessToken || res.data?.accessToken;

      if (!token) {
        // Fallback: auto-login after register if token wasn't included
        const loginRes = await api.login(formData.email, formData.password);
        const loginToken = loginRes.accessToken || loginRes.data?.accessToken;
        setAuthToken(loginToken);
        setUser(loginRes.user || loginRes.data?.user || { email: formData.email, firstName: formData.firstName });
      } else {
        setAuthToken(token);
        setUser(res.user || res.data?.user || { email: formData.email, firstName: formData.firstName });
      }

      onAuthSuccess();
    } catch (err) {
      setError(err.message || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#F4F0EA] flex items-center justify-center p-4">
      <div className="max-w-4xl w-full grid grid-cols-1 md:grid-cols-2 gap-8 items-center">
        
        {/* Left Side: Brand Teaser */}
        <div className="space-y-6">
          <div className="inline-flex items-center gap-2 bg-[#FFE600] border-2 border-black px-3 py-1 rounded-full nb-shadow-sm">
            <Zap className="w-4 h-4 text-black fill-black" />
            <span className="text-xs font-black uppercase tracking-wider">Neo-Brutalist Finance</span>
          </div>

          <h1 className="text-5xl font-black text-black leading-none tracking-tight">
            TAKE CONTROL OF YOUR <span className="bg-[#00E5FF] px-2 border-2 border-black inline-block mt-2">MONEY.</span>
          </h1>

          <p className="text-lg font-semibold text-gray-800">
            Track expenses, manage budgets, and record transactions instantly with web & Telegram bot synchronization.
          </p>

          <div className="space-y-3">
            <div className="nb-card bg-white p-3 flex items-center gap-3">
              <div className="bg-[#FFE600] border-2 border-black p-2 rounded">
                <Send className="w-5 h-5" />
              </div>
              <div>
                <h4 className="font-black text-sm uppercase">Telegram Bot Linked</h4>
                <p className="text-xs font-semibold text-gray-600">Send <code className="bg-gray-100 px-1 border border-black rounded">/spent 450 Food</code> in Telegram</p>
              </div>
            </div>

            <div className="nb-card bg-white p-3 flex items-center gap-3">
              <div className="bg-[#00E5FF] border-2 border-black p-2 rounded">
                <ShieldCheck className="w-5 h-5" />
              </div>
              <div>
                <h4 className="font-black text-sm uppercase">Budget Alerts</h4>
                <p className="text-xs font-semibold text-gray-600">Real-time status tags: ON_TRACK, WARNING, OVER_BUDGET</p>
              </div>
            </div>
          </div>
        </div>

        {/* Right Side: Auth Card */}
        <div className="nb-card bg-white p-6 md:p-8 space-y-6">
          {/* Mode Selector */}
          <div className="flex border-2 border-black rounded-lg p-1 bg-[#F4F0EA]">
            <button
              onClick={() => { setIsLogin(true); setError(''); }}
              className={`flex-1 py-2 font-black uppercase text-sm rounded transition-all ${
                isLogin ? 'bg-[#FFE600] border-2 border-black nb-shadow-sm' : 'text-gray-600'
              }`}
            >
              Sign In
            </button>
            <button
              onClick={() => { setIsLogin(false); setError(''); }}
              className={`flex-1 py-2 font-black uppercase text-sm rounded transition-all ${
                !isLogin ? 'bg-[#FFE600] border-2 border-black nb-shadow-sm' : 'text-gray-600'
              }`}
            >
              Register
            </button>
          </div>

          <h2 className="text-2xl font-black text-black">
            {isLogin ? 'WELCOME BACK 👋' : 'CREATE YOUR ACCOUNT 🚀'}
          </h2>

          {error && (
            <div className="bg-[#FF2A85] text-white border-2 border-black p-3 rounded font-bold text-sm nb-shadow-sm">
              ⚠️ {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            {!isLogin && (
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-black uppercase mb-1">First Name</label>
                  <input
                    type="text"
                    required
                    className="nb-input"
                    placeholder="Om"
                    value={formData.firstName}
                    onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                  />
                </div>
                <div>
                  <label className="block text-xs font-black uppercase mb-1">Last Name</label>
                  <input
                    type="text"
                    required
                    className="nb-input"
                    placeholder="Patil"
                    value={formData.lastName}
                    onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                  />
                </div>
              </div>
            )}

            <div>
              <label className="block text-xs font-black uppercase mb-1">Email Address</label>
              <input
                type="email"
                required
                className="nb-input"
                placeholder="om@example.com"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              />
            </div>

            <div>
              <label className="block text-xs font-black uppercase mb-1">Password</label>
              <div className="relative">
                <input
                  type={showPassword ? 'text' : 'password'}
                  required
                  className="nb-input pr-12"
                  placeholder="••••••••"
                  value={formData.password}
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-black hover:opacity-80 p-1"
                  title={showPassword ? 'Hide password' : 'View password'}
                >
                  {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="nb-btn nb-btn-primary w-full py-3 text-base justify-center mt-2"
            >
              {loading ? 'Processing...' : (isLogin ? 'Sign In Now' : 'Create Account')}
              <ArrowRight className="w-5 h-5" />
            </button>
          </form>
        </div>

      </div>
    </div>
  );
}
